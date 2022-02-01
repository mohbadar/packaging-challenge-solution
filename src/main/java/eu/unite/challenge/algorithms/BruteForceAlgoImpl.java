package eu.unite.challenge.algorithms;


import eu.unite.challenge.dataobjects.Item;
import eu.unite.challenge.dataobjects.RecordInstance;
import net.jcip.annotations.Immutable;

import java.math.BigDecimal;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * This class is the simplest extension of {@link AbstractProblemSolver},
 * and solves the problem by exhaustively searching the solution space.
 * Each item can be either in the solution or not. So, for N items,
 * there are 2<sup>N</sup> possible solution. For each solution, the cost and
 * weight are computed, and the winner is the one with highest cost
 * (and if several such solutions exist, the one with least weight).
 * For N = 15, there are at most 32,768 possible solutions.
 * The algorithm needs only a few milliseconds (on a Surface Pro 7
 * laptop) to run. This class is used in unit tests to check the
 * correctness of other algorithms on thousands of random problem instances.
 */
@Immutable
public final class BruteForceAlgoImpl extends AbstractProblemSolver {
    public BruteForceAlgoImpl(final RecordInstance problemInstance) {
        super(problemInstance);
    }

    protected SortedSet<Integer> solve(final RecordInstance problemInstance) {
        final List<Item> items = problemInstance.getItems();
        final int count = items.size();

        SortedSet<Integer> solution = new TreeSet<>();
        BigDecimal maxPrice = BigDecimal.ZERO;
        BigDecimal itsWeight = BigDecimal.ZERO;

        /*
         * Each subset of items is identified by i
         * If in the binary expansion of i, the jth
         * bit is 0, it means the jth item is not
         * included in the subset. Otherwise, it is
         * included.
         */
        for (long i = 0; i < (1L << count); i++) {
            BigDecimal totalWeight = new BigDecimal(0);
            BigDecimal totalPrice = new BigDecimal(0);
            SortedSet<Integer> subset = new TreeSet<>();

            /*
             * j denotes the index of an item.
             * As explained about i, the jth bit
             * of i shows whether the jth item
             * should be included.
             */
            for (int j = 0; j < count; j++) {
                long mask = (i & (1L << j));
                Item item = items.get(j);
                BigDecimal next = totalWeight.add(item.getWeight());
                if (mask != 0 && next.compareTo(problemInstance.getMaxWeight()) <= 0) {
                    subset.add(item.getNumber());
                    totalWeight = next;
                    totalPrice = totalPrice.add(item.getPrice());
                }
            }
            if ((maxPrice.compareTo(totalPrice) < 0) ||
                    (maxPrice.compareTo(totalPrice) == 0 && itsWeight.compareTo(totalWeight) > 0)) {
                maxPrice = totalPrice;
                itsWeight = totalWeight;
                solution = subset;
            }
        }

        return solution;
    }
}