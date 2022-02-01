package eu.unite.challenge.dataobjects;

import lombok.Getter;
import net.jcip.annotations.Immutable;
import org.springframework.lang.Nullable;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * This class receives map of labels-to-items (@code map), and a list of item labels
 * (@{code labels}). It creates a "package" of the corresponding items, and
 * computes the total weight and price of items in the package. It also computes
 * {@code result} as the CSV version of {@code indices}.
 */
@Immutable
@Getter
public final class Package {
    /*
     * Immutability for `indices` is guaranteed by initialization,
     * which is in one of these forms:
     *     1) Collections.emptyList()
     *     2) Collections.unmodifiableList(some-other-list)
     */

    // A sorted set of indices in the bag
    private final SortedSet<Integer> indices;

    // CSV format of elements in indices
    private final String result;

    // Total weight of the bag
    private final BigDecimal resultWeight;

    // Total price of the bag
    private final BigDecimal resultPrice;

    /**
     * Constructs a {@code Package} using items whose labels are specified.
     * Specifically, converts {@code sortedLabels} to CSV format and stores it
     * in {@code result}, and fetches items from {@code map} based on their labels
     * to compute their total price and weight.
     *
     * @param map    Map of labels-to-items
     * @param sortedLabels Label of items to include in the bag, sorted
     *                     from smallest to largest
     */
    public Package(final Map<Integer, Item> map,
                   @Nullable final SortedSet<Integer> sortedLabels) {

        if(map == null)
            throw new NullPointerException("Argument 'map' cannot be null.");

        if (sortedLabels == null || sortedLabels.isEmpty()) {
            indices = Collections.emptySortedSet();
            result = "-";
            resultWeight = resultPrice = BigDecimal.ZERO;
        } else {
            if(!map.keySet().containsAll(sortedLabels))
                throw new IllegalArgumentException("The labels must be a subset of the key-set of map.");

            SortedSet<Integer> tmpLabels = new TreeSet<>(sortedLabels);
            indices = Collections.unmodifiableSortedSet(tmpLabels);

            StringBuilder sb = new StringBuilder();
            String delimiter = "";
            BigDecimal weight = BigDecimal.ZERO;
            BigDecimal price = BigDecimal.ZERO;

            /*
             * The following loop creates a comma-separated vales (CSV)
             * version of indices in the given StringBuilder ({@code sb}),
             * and also computes the total {@code weight} and total
             * {@code price} corresponding to items whose index are in
             * {@code indices}.
             *
             * While we could use Strings.join(",", indices) to
             * create a CSV version of indices, it is more efficient
             * to loop over indices. This enables us to compute
             * everything in a single pass.
             */
            for (int i : indices) {
                sb.append(delimiter).append(i);
                delimiter = ",";
                weight = weight.add(map.get(i).getWeight());
                price = price.add(map.get(i).getPrice());
            }

            result = sb.toString();
            resultPrice = price;
            resultWeight = weight;
        }
    }

    @Override
    public String toString() {
        return String.format("Price = %s, weight = %s, result = (%s).",
                resultPrice.toPlainString(), resultWeight.toPlainString(), result
        );
    }
}
