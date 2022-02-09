package eu.unite.challenge.algorithms;

import eu.unite.challenge.dataobjects.Item;
import eu.unite.challenge.dataobjects.RecordInstance;
import net.jcip.annotations.Immutable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * This class uses the greedy approach explained previously,
 * with one twist: It greedily picks a subset of items until
 * the weight constraint allows no more. It then compares the
 * cost of this subset with the item with maximum cost, and
 * the winner is returned. It can be shown that if this
 * comparison is not made, the solution can be arbitrarily bad.
 * However, the comparison allows a 1/2-approximation scheme.
 * The unit tests show that this approximation factor is
 * achieved over thousands of random problem instances.
 */
@Immutable
public final class GreedyApproximationAlgoImpl extends AbstractProblemSolver {
    private static final Logger logger = LoggerFactory.getLogger(GreedyApproximationAlgoImpl.class);

    public GreedyApproximationAlgoImpl(final RecordInstance recordInstance) {
        super(recordInstance);
    }

    /**
     * @inheritDoc
     */
    protected SortedSet<Integer> solve(final RecordInstance problemInstance) {
        /*
         * Sort items in decreasing order of efficiency.
         * If two items have the same efficiency, prefer the
         * one with higher price.
         */
        final List<Item> sorted = problemInstance.getItems().stream()
                .sorted(ItemComparators.efficiencyPrice.reversed())
                .collect(Collectors.toUnmodifiableList());

        logger.trace("sorted = {}.", sorted);

        final SortedSet<Integer> indices = new TreeSet<>();
        BigDecimal weight = BigDecimal.ZERO;
        BigDecimal price = BigDecimal.ZERO;

        BigDecimal maxPrice = BigDecimal.ZERO;
        int maxLabel = -1;

        /*
         * Iterate over the sorted items, and pick them if
         * the resulting subset does not violate the
         * maximum weight constraint.
         */
        for (Item item : sorted) {
            BigDecimal tmpWeight = weight.add(item.getWeight());
            if (tmpWeight.compareTo(problemInstance.getMaxWeight()) <= 0) {
                weight = tmpWeight;
                indices.add(item.getNumber());
                price = price.add(item.getPrice());
            }
            // keep an eye on the item with maximum price
            if (maxPrice.compareTo(item.getPrice()) < 0) {
                maxLabel = item.getNumber();
                maxPrice = item.getPrice();
            }
        }

        logger.trace("breakPrice = {}.", maxPrice);
        logger.trace("breakNumber = {}.", maxLabel);
        logger.trace("Price = {}.", price);
        logger.trace("Solution = {}.", indices);

        /*
         * If the resulting subset of items from the heuristic
         * has lower price than the max-price item, just return
         * the latter.
         *
         * This check ensures 1/2 approximation factor.
         */
        if (maxPrice.compareTo(price) > 0)
            return new TreeSet<>(Collections.singleton(maxLabel));

        return indices;
    }

}
