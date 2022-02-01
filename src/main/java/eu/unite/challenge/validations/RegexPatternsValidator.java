package eu.unite.challenge.validations;


import net.jcip.annotations.Immutable;

import java.util.regex.Pattern;

/**
 * Class for storing static and compiled regular expression patterns
 */
@Immutable
public final class RegexPatternsValidator {
    // private constructor to prevent instantiation
    private RegexPatternsValidator() {
    }

    // regular expression for a positive integer
    private static final String INT_REGEX = "[1-9]\\d*";

    // regular expression for a non-negative double
    private static final String DECIMAL_REGEX = "(?:\\d+(?:\\.\\d+)?|\\.\\d+)";

    // regular expression for a pair of parentheses, and their contents
    private static final String PARENTHESIS_REGEX = "(?:\\((.*?)\\))";

    /*
     * A single line has format
     *   W : (1,w1,€p1)...(m,wm,€pm)
     * where
     *  - W is has DECIMAL_PATTERN
     *  - each single item has SINGLE_ITEM_PATTERN
     *  - all items together have ALL_ITEMS_PATTERN
     */
    public static final Pattern DECIMAL_PATTERN = Pattern.compile("^" + DECIMAL_REGEX + "$");
    public static final Pattern SINGLE_ITEM_PATTERN = Pattern.compile(PARENTHESIS_REGEX);
    public static final Pattern ALL_ITEMS_PATTERN = Pattern.compile("^" + PARENTHESIS_REGEX + "+$");


    /*
     * An item is represented by a triple n,w,€p
     *   - n is the label (or index/number) of the item
     *   - w is the weight of the item
     *   - p is the price of the item
     * The following patterns match each of these elements
     */
    public static final Pattern LABEL_PATTERN = Pattern.compile("^" + INT_REGEX + "$");
    public static final Pattern WEIGHT_PATTERN = Pattern.compile("^" + DECIMAL_REGEX + "$");
    public static final Pattern PRICE_PATTERN = Pattern.compile("^€" + DECIMAL_REGEX + "$");
}
