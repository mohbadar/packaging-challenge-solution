package eu.unite.challenge.algorithms;

import eu.unite.challenge.dataobjects.Item;
import eu.unite.challenge.dataobjects.RecordInstance;
import lombok.Getter;
import net.jcip.annotations.Immutable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

import static eu.unite.challenge.validations.ConstraintRules.SCALE;

/**
 * {@code BranchAndBoundAlgoImpl} is similar to {@link BruteForceAlgoImpl},
 * but it uses heuristics so that only plausible solutions
 * in the solution space are traversed. The order of
 * traversal is also optimized. The solution space can be
 * seen as a binary tree. For node {@code i}, the left edge denotes
 * leaving the {@code i+1} item, while the right edge denotes
 * taking it. For each node, a bound is computed using
 * the heuristic similar to { GreedyApproximationAlgoImpl}
 * (but where we can pick a fraction of items). A subtree
 * is pruned if
 * <ol>
 *     <li>It violates the weight constraint, or</li>
 *     <li>Its bound is less than the current maximum cost
 *  achieved by traversing other nodes of the tree</li>
 * </ol>
 */
@Immutable
public final class BranchAndBoundAlgoImpl extends AbstractProblemSolver {

    private BigDecimal maxWeight;
    private List<Item> sorted;
    private Queue<Node> queue;
    private BigDecimal bestPrice;
    private BigDecimal bestWeight;
    private SortedSet<Integer> bestLabels;

    public BranchAndBoundAlgoImpl(final RecordInstance problemInstance) {
        super(problemInstance);
    }

    /**
     * @inheritDoc
     */
    @Override
    protected SortedSet<Integer> solve(final RecordInstance problemInstance) {
        /*
         * Sort the items by their efficiency, then price.
         * This will *possibly* help the algorithm to achieve
         * its solution faster.
         */
        sorted = problemInstance.getItems().stream()
                .sorted(ItemComparators.efficiencyPrice.reversed())
                .collect(Collectors.toUnmodifiableList());

        /*
         * Create a priority queue, where items are retrieved
         * in the following order:
         * Higher bound, higher prices, lower weight.
         * This will *possibly* help the algorithm to achieve
         * its solution faster.
         */
        maxWeight = problemInstance.getMaxWeight();
        queue = new PriorityQueue<>(Node.boundPriceWeight.reversed());

        /*
         * Create a dummy node and add it to the queue.
         * Note the similarity with BFS search of a graph:
         * https://en.wikipedia.org/wiki/Breadth-first_search#Pseudocode
         * The only difference is that the queue is not FIFO.
         */
        Node dummy = new Node();
        queue.add(dummy);
        bestPrice = BigDecimal.ZERO;

        while (!queue.isEmpty()) {
            Node parent = queue.poll();

            /*
             * Prune the subtree rooted at "parent" if
             * (1) It is a leaf of the tree
             * (2) It's maximum price is bounded by an amount less than
             *     the current best price.
             */
            if (parent.level >= sorted.size() - 1
                    || parent.bound.compareTo(bestPrice) < 0) {
                continue;
            }

            // What if we leave the ith item in sorted least?
            processChild(parent, true);

            // What if we take the ith item in sorted least?
            Node n = processChild(parent, false);

            /*
             * If the node has higher price than bestPrice,
             * or if the node price = bestPrice, but this node is lighter,
             * replace the best result.
             */
            if (n != null) {
                final int cmp = n.price.compareTo(bestPrice);
                if ((cmp > 0) || (cmp == 0 && n.weight.compareTo(bestWeight) < 0)) {
                    bestPrice = n.price;
                    bestWeight = n.weight;
                    bestLabels = n.labels;
                }
            }
        }

        return bestLabels;
    }

    /**
     * Process a child of {@code parent}. There are two possible cases:
     * <ol>
     *     <li><b>Leave child:</b> In this case, the bound on the next level
     *     is computed using {@code computeBound}. The subtree is pruned if
     *     this bound is smaller than the {@code bestPrice} attained so far,
     *     and {@code null} is returned. Otherwise, the information related tp
     *     the item is returned as an instance of {@code Node}.
     *     </li>
     *     <li><b>Take child:</b> In this case, we first check whether adding
     *     the child to the solution violates the weight constraint, and if so
     *     {@code null} is returned. Otherwise, the child is added, remaining
     *     weight and achieved price are updates, and the rest is as above.
     *     </li>
     * </ol>
     *
     * @param parent     The parent node.
     * @param leaveChild Whether the method should consider leaving a child.
     * @return A {@code Node} to be added to search space, or {@code null} if
     * the subtree is to be pruned.
     */
    private Node processChild(final Node parent, final boolean leaveChild) {
        final int i = parent.level + 1;
        final BigDecimal weightToRoot;
        final BigDecimal priceToRoot;
        final SortedSet<Integer> labelsToRoot;

        if (leaveChild) {
            weightToRoot = parent.weight;
            priceToRoot = parent.price;
            labelsToRoot = parent.labels;
        } else {
            final Item item = sorted.get(i);
            weightToRoot = parent.weight.add(item.getWeight());

            if (weightToRoot.compareTo(maxWeight) > 0)
                return null;

            labelsToRoot = new TreeSet<>(parent.labels);
            labelsToRoot.add(item.getNumber());
            priceToRoot = parent.price.add(item.getPrice());
        }

        BigDecimal bound = priceToRoot.add(computeBound(i + 1, weightToRoot));
        if (bound.compareTo(bestPrice) >= 0) {
            final Node n = new Node(i, weightToRoot, priceToRoot, bound, labelsToRoot);
            queue.add(n);
            return n;
        }

        return null;
    }

    /**
     * Compute the maximum attainable price, from this node downward.
     * The algorithm uses a greedy heuristic, see
     * <a href="https://en.wikipedia.org/wiki/Knapsack_problem#Greedy_approximation_algorithm">wiki</a>.
     *
     * @param start         The index of the starting item.
     * @param currentWeight The current weight of the items in the solution subset.
     * @return A bound on the maximum attainable price.
     */
    BigDecimal computeBound(final int start, final BigDecimal currentWeight) {
        BigDecimal maxPrice = BigDecimal.ZERO;
        BigDecimal remainingWeight = maxWeight.subtract(currentWeight);

        for (int i = start; i < sorted.size(); i++) {
            Item item = sorted.get(i);
            BigDecimal price = item.getPrice();
            final BigDecimal weight = item.getWeight();

            if (weight.compareTo(remainingWeight) > 0) {
                BigDecimal fraction = remainingWeight.divide(weight, SCALE, RoundingMode.HALF_UP);
                price = price.multiply(fraction);
                maxPrice = maxPrice.add(price);
                break;
            }
            remainingWeight = remainingWeight.subtract(weight);
            maxPrice = maxPrice.add(price);
        }

        return maxPrice;
    }

    /**
     * A class representing a node in the solution tree
     */
    @Immutable
    static final class Node {
        static final Comparator<Node> boundPriceWeight =
                Comparator.comparing(Node::getBound)
                        .thenComparing(Node::getPrice)
                        .thenComparing(Node::getWeight, Comparator.reverseOrder());
        @Getter
        final BigDecimal weight;
        @Getter
        final BigDecimal price;
        @Getter
        final BigDecimal bound;
        final int level;
        final SortedSet<Integer> labels;

        Node() {
            level = -1;
            weight = price = bound = BigDecimal.ZERO;
            labels = Collections.unmodifiableSortedSet(new TreeSet<>());
        }

        public Node(int level, BigDecimal weight, BigDecimal price, BigDecimal bound, SortedSet<Integer> labels) {
            this.level = level;
            this.weight = weight;
            this.price = price;
            this.bound = bound;
            this.labels = Collections.unmodifiableSortedSet(new TreeSet<>(labels));
        }
    }
}

