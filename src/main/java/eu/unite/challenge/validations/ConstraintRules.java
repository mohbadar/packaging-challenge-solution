package eu.unite.challenge.validations;

import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Constraints Rules for the Solution
 */
public class ConstraintRules {
    private ConstraintRules() {
    }


    // Maximum weight of package
    public static final BigDecimal MAX_PACKAGE_WEIGHT = BigDecimal.valueOf(100);

    // Maximum items per line
    public static final int MAX_ITEMS_PER_LINE = 15;

    // Maximum weight of an item
    public static final BigDecimal MAX_ITEM_WEIGHT = BigDecimal.valueOf(100);

    // Maximum price of an item
    public static final BigDecimal MAX_ITEM_PRICE = BigDecimal.valueOf(100);

    // Default file encoding, used for reading/writing to files
    public static final Charset FILE_ENCODING = StandardCharsets.UTF_8;

    // Maximum input file size, in bytes
    public static final long MAX_FILE_SIZE_BYTES = 1_000_000;

    /**
     * How many digits to keep digits after the decimal point, when dividing numbers together?
     */
    public static final int SCALE = 8;

    /*
     * DynamicProgrammingAlgoImpl requires a table of order O(N*W),  where N is the number of items, and W is the possible number of values for weight.
     * The following constant  gives an upper bound for W.
     */
    public static final int MAX_INT_WEIGHT_FOR_DP = 10000;

}
