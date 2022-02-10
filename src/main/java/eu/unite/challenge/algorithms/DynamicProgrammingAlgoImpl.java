package eu.unite.challenge.algorithms;

import eu.unite.challenge.dataobjects.Item;
import eu.unite.challenge.dataobjects.RecordInstance;
import eu.unite.challenge.exceptions.OutOfRangeProblemSizeException;
import net.jcip.annotations.Immutable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static eu.unite.challenge.validations.ConstraintRules.MAX_INT_WEIGHT_FOR_DP;

/**
 * is a pseudo-polynomial algorithm: It is polynomial time
 * in the value W, which denotes the number of possible
 * weights (if the weights are fractional, we can multiply
 * them by a common factor so that they are all integers.)
 * It works by trading space for time: A large table is
 * used to memoize state for subproblems. For this specific
 * problem, the parameters are such that the running time
 * of {@code DynamicProgrammingAlgoImpl} is worse than that of {@link BruteForceAlgoImpl},
 * and it even uses much more RAM.
 */
@Immutable
public class DynamicProgrammingAlgoImpl extends AbstractProblemSolver {
    private static final Logger logger = LoggerFactory.getLogger(DynamicProgrammingAlgoImpl.class);

    public DynamicProgrammingAlgoImpl(final RecordInstance recordInstance) {
        super(recordInstance);
    }

    /**
     * This method implements the actual dynamic programming code
     *
     * @param maxWeight      The maximum weight of items
     * @param maxWeightScale The maximum over the scale of all items,
     *                       as well as {@code maxWeight}
     * @param items          List of items
     * @return A sorted set of indices corresponding to the best items
     */
    static SortedSet<Integer> dpSolve(final BigDecimal maxWeight,
                                      final int maxWeightScale,
                                      final List<Item> items) {

        /*
         * Since weights might be non-integers, we first find a
         * common "multiplier": A number which, if multiplied
         * by all weights, turns them into integers.
         *
         * Example: for weights = {1.3, 8.95, 2}, the multiplier = 100,
         * as multiplying all weights by 100 results in integers.
         */
        final BigDecimal multiplier = BigDecimal.TEN.pow(maxWeightScale);
        final int intMaxWeight = toInt(maxWeight, multiplier);

        // The int value of maximum weight should not exceed some configured bound
        checkMaxWeight(intMaxWeight);

        /*
         * Items are first sorted by their price, then weight.
         * This ensure that if there are multiple subsets with
         * the same price, the one which is lighter wins.
         */
        final List<Item> sorted = items.stream()
                .sorted(ItemComparators.priceWeight.reversed())
                .collect(Collectors.toUnmodifiableList());

        logger.trace("{}", sorted);

        /*
         * The table priceMemo[i][w] keeps the maximum price attainable by
         * including items 1,...,i while restricting to weight w.
         *
         * Initially, it is all zeros. Each element is computed by the
         * following recursion:
         *
         * if w >= w_i:
         *      priceMemo[i][w] = max(priceMemo[i-1][w], v_i + priceMemo[i-1][w-w_i])
         * else:
         *      priceMemo[i][w] = priceMemo[i-1][w]
         */
        final BigDecimal[][] priceMemo = new BigDecimal[sorted.size() + 1][intMaxWeight + 1];
        final boolean[][] keep = new boolean[sorted.size() + 1][intMaxWeight + 1];
        final int[] intWeights = new int[sorted.size()];

        for (int i = 0; i < sorted.size(); i++) {
            final Item item = sorted.get(i);
            final BigDecimal price = item.getPrice();
            final int weight = toInt(item.getWeight(), multiplier);
            intWeights[i] = weight;
            for (int j = 0; j <= intMaxWeight; j++) {
                if (priceMemo[i][j] == null)
                    priceMemo[i][j] = BigDecimal.ZERO;
                if (weight > j)
                    priceMemo[i + 1][j] = priceMemo[i][j];
                else {
                    BigDecimal leave = priceMemo[i][j];
                    BigDecimal take = price.add(priceMemo[i][j - weight]);
                    if (take.compareTo(leave) > 0) {
                        priceMemo[i + 1][j] = take;
                        keep[i + 1][j] = true;
                    } else
                        priceMemo[i + 1][j] = leave;
                }
            }
        }

        return findIncluded(intMaxWeight, sorted, intWeights, keep);
    }

    /**
     * Check if the integer value of maximum weight exceeds some configured bound.
     *
     * @param intMaxWeight The integer value of maximum weight.
     * @throws OutOfRangeProblemSizeException If the check fails.
     */
    private static void checkMaxWeight(int intMaxWeight) {
        final String description = "This means that the dynamic programming approach will use " +
                "an unacceptable amount of CPU & memory.";

        if (intMaxWeight > MAX_INT_WEIGHT_FOR_DP)
            throw new OutOfRangeProblemSizeException(String.format("The integer maximum weight %d exceeds the configured amount %d. %s",
                    intMaxWeight, MAX_INT_WEIGHT_FOR_DP, description));
    }

    /**
     * Converts the real number {@code num} to int, by multiplying it
     * with {@code multiplier}.
     *
     * @param num        The number to be converted to int.
     * @param multiplier The multiplying factor.
     * @return The integer value of {@code num}.
     * @throws ArithmeticException if {@code num*multiplier} has a nonzero
     *                             fractional part, or will not fit in an {@code int}.
     */
    static int toInt(final BigDecimal num, final BigDecimal multiplier) {
        return num.multiply(multiplier).intValueExact();
    }

    /**
     * Traverses the table generated by the dynamic programming (DP),
     * to find indices for the solution subset.
     * <p>
     * <b>Algorithm:</b> {@code keep[i][w]} is 1 if DP decides to take item i
     * while restricting the weight to w.
     * Therefore, we simply start at the end of the table: keep[n][W].
     * If it is true, we set w = W - w<sub>n</sub>.
     * The procedure continues until i reaches 0.
     *
     * @param maxWeight  The maximum weight of the package.
     * @param items      The list of the items.
     * @param intWeights Item weights, converted to integer.
     * @param keep       The table generated by the dynamic programming.
     * @return The indices for the solution subset.
     */
    static SortedSet<Integer> findIncluded(int maxWeight, List<Item> items, int[] intWeights, boolean[][] keep) {
        int remainingWeight = maxWeight;
        SortedSet<Integer> indices = new TreeSet<>();
        for (int i = items.size(); i >= 1; i--)
            if (keep[i][remainingWeight]) {
                indices.add(items.get(i - 1).getNumber());
                remainingWeight -= intWeights[i - 1];
            }
        return indices;
    }

    /**
     * @inheritDoc
     */
    @Override
    protected SortedSet<Integer> solve(final RecordInstance recordInstance) {
        final int maxWeightScale = recordInstance.getMaxWeightScale();
        final List<Item> items = recordInstance.getItems();
        return dpSolve(recordInstance.getMaxWeight(), maxWeightScale, items);
    }

}
