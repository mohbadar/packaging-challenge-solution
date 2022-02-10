package eu.unite.challenge.algorithms;


import eu.unite.challenge.dataobjects.Item;

import java.util.Comparator;

/**
 * Keeps comparators used by various algorithms.
 * For instance, {@code priceWeight} is a comparator which
 * first compares two items using their price,
 * and if the prices are equal compares their weight.
 */
public final class ItemComparators {

    // If their price is equal, the lighter one wins
    public static final Comparator<Item> priceWeight =
            Comparator.comparing(Item::getPrice).thenComparing(Item::getWeight, Comparator.reverseOrder());
    // If their efficiency is equal, the expensive one wins
    public static final Comparator<Item> efficiencyPrice =
            Comparator.comparing(Item::getEfficiency).thenComparing(Item::getPrice);
    private ItemComparators() {
    }

}
