package eu.unite.challenge.dataobjects;

import eu.unite.challenge.exceptions.ItemException;
import lombok.Getter;
import net.jcip.annotations.Immutable;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

import static eu.unite.challenge.validations.ConstraintRules.*;
import static eu.unite.challenge.validations.RegexPatternsValidator.*;

/**
 * This class parses triples "n,w,p",
 * and stores them as the three fields number, weight, and price. <p>
 * It also computes efficiency = p/w.
 */
@Getter
@Immutable
public final class Item implements Serializable {
    private static final long serialVersionUID = -4534989690893969984L;

    private final int number;
    private final BigDecimal weight;
    private final BigDecimal price;

    private final transient BigDecimal efficiency;

    /**
     * A simple constructor, which checks input ranges, and assigns
     * the arguments directly to fields. It also computes efficiency.
     *
     * @param number Item's number n
     * @param weight Item's weight w
     * @param price  Item's price p
     * @throws ItemException If any of the items are out of bounds.
     * See {@link #checkRanges} for further details.
     */
    public Item(final int number, final BigDecimal weight, final BigDecimal price) throws ItemException {
        checkRanges(number, weight, price);
        this.number = number;
        this.weight = weight;
        this.price = price;
        this.efficiency = computeEfficiency();
    }

    /**
     * @param itemNo Item index, in the list of items (1-based)
     * @param triple A string in format "n,w,€p" where
     *               n is the item number (must be identical to {@code itemNo}
     *               w is the item weight
     *               p is the item profit
     * @throws ItemException if {@code triple} is not in proper format
     */
    public Item(final int itemNo, final String triple) throws ItemException {
        if (triple == null)
            throw new ItemException(itemNo, "item cannot be null");

        String strippedTriple = triple.replaceAll("\\s+", "");
        String[] subItems = strippedTriple.split(",");
        if (subItems.length != 3)
            throw new ItemException(itemNo,
                    String.format("Expected 3 components, but received %d", subItems.length));

        if (!LABEL_PATTERN.matcher(subItems[0]).matches())
            throw new ItemException(itemNo, "Item number must be a positive integer");

        if (!WEIGHT_PATTERN.matcher(subItems[1]).matches())
            throw new ItemException(itemNo, "Item weight must be a number");

        if (!PRICE_PATTERN.matcher(subItems[2]).matches())
            throw new ItemException(itemNo, "Item price must be a number, preceded with €");

        int num = Integer.parseInt(subItems[0]);
        BigDecimal w = new BigDecimal(subItems[1]);
        // use substring to discard the € sign
        BigDecimal p = new BigDecimal(subItems[2].substring(1));

        if (num != itemNo)
            throw new ItemException(itemNo, "Item number does not match its position");

        this.number = num;
        this.weight = w;
        this.price = p;
        this.efficiency = computeEfficiency();
    }

    /**
     * Computes efficiency = price/weight.
     * May involving rounding up the result.
     * Up to {@code SCALE} digits after decimal
     * point are used.
     *
     * @return efficiency
     */
    private BigDecimal computeEfficiency() {
        return price.divide(weight, SCALE, RoundingMode.HALF_UP);
    }

    /**
     * @param number Item's number n. Must be 1<=n<=MAX_ITEMS_PER_LINE
     * @param weight Item's weight w. Must be 0 < w <= MAX_ITEM_WEIGHT
     * @param price  Item's price p. Must be 0 < p < MAX_ITEM_PRICE
     * @throws ItemException If any of the items are out of bounds.
     */
    public static void checkRanges(final int number, final BigDecimal weight, final BigDecimal price)
            throws ItemException {
        if (number <= 0)
            throw new ItemException(number, "Item number must be a positive");
        if (number > MAX_ITEMS_PER_LINE)
            throw new ItemException(number, String.format("Item number = %d is greater than the maximum allowed %d",
                    number, MAX_ITEMS_PER_LINE));
        if (weight.compareTo(BigDecimal.ZERO) <= 0)
            throw new ItemException(number, "Item weight must be positive");
        if (price.compareTo(BigDecimal.ZERO) <= 0)
            throw new ItemException(number, "Item price must be positive");
        if (weight.compareTo(MAX_ITEM_WEIGHT) > 0)
            throw new ItemException(number, String.format(
                    "Package weight %s exceeds %s", weight.toPlainString(), MAX_ITEM_WEIGHT.toPlainString()));
        if (price.compareTo(MAX_ITEM_PRICE) > 0)
            throw new ItemException(number, String.format(
                    "Package price %s exceeds %s", weight.toPlainString(), MAX_ITEM_WEIGHT.toPlainString()));
    }

    String tripleString() {
        return String.format("%d, %s, €%s", number, weight.toPlainString(), price.toPlainString());
    }

    @Override
    public String toString() {
        return String.format("(%s)", tripleString());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return number == item.number &&
                weight.compareTo(item.weight) == 0 &&
                price.compareTo(item.price) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(number, weight, price);
    }
}
