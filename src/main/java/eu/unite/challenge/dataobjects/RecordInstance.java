package eu.unite.challenge.dataobjects;

import eu.unite.challenge.exceptions.ItemException;
import eu.unite.challenge.exceptions.LineFormatException;
import lombok.Getter;
import net.jcip.annotations.Immutable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

import static eu.unite.challenge.validations.ConstraintRules.*;
import static eu.unite.challenge.validations.RegexPatternsValidator.*;

/**
 * This class reads an input line as a {@code String}, and parses
 * it into a maximum weight {@code maxWeight} and a
 * list of items ({@code List<Item> items}).
 * It also keeps a mapping {@code Map<Integer, Item> map} for fast
 * retrieval of items given their label.
 *
 * @see Item
 */
@Immutable
@Getter
public final class RecordInstance implements Serializable {
    private static final long serialVersionUID = -5615641040835621539L;

    private static final transient Logger logger = LoggerFactory.getLogger(RecordInstance.class);

    private final BigDecimal maxWeight;
    private final List<Item> items;
    private final Map<Integer, Item> map;

    private final transient int maxWeightScale;

    /**
     * Creates a problem instance using the line number and the line itself.
     *
     * @param lineNo The number of line in the input file.
     *               In case of errors, it is used to direct the user
     *               to the malformed line.
     * @param line   The line itself
     * @throws LineFormatException If the line is malformed
     */
    public RecordInstance(final int lineNo, final String line) throws LineFormatException {
        /*
         * For a well-formed line of the form a : b,
         * section[0] = a
         * section[1] = b
         */
        String[] sections = getSections(lineNo, line);

        maxWeight = new BigDecimal(sections[0]);
        if (maxWeight.compareTo(MAX_PACKAGE_WEIGHT) > 0)
            throw new LineFormatException(
                    lineNo, String.format("Max package weight %s exceeds %s",
                    maxWeight.toPlainString(), MAX_PACKAGE_WEIGHT.toPlainString()));


        int scale = maxWeight.scale();
        logger.trace("Line #{}: maxWeight = {}", lineNo, maxWeight);

        /*
         * Use proper RegEx to split the line into items
         */
        Matcher matcher = getMatcher(lineNo, sections[1]);
        items = new ArrayList<>();
        int cnt;

        for (cnt = 0; matcher.find(); cnt++) {
            Item item;
            try {
                item = new Item(cnt + 1, matcher.group(1));
            } catch (ItemException e) {
                throw new LineFormatException(lineNo, e);
            }
            if (item.getWeight().compareTo(maxWeight) <= 0)
                items.add(item);

            scale = Math.max(scale, item.getWeight().scale());
        }

        if (cnt > MAX_ITEMS_PER_LINE)
            throw new LineFormatException(lineNo,
                    String.format("At most %d items are allowed per line, but received %d",
                            MAX_ITEMS_PER_LINE, cnt));

        logger.trace("Line #{}: Items = {}", lineNo, items);

        maxWeightScale = scale;
        logger.trace("maxWeightScale = {}", maxWeightScale);

        map = items.stream()
                .collect(Collectors.toMap(Item::getNumber, item -> item));
    }

    /**
     *
     * @param lineNo Line number, used to give meaningful errors
     * @param triples The triples part of a line, having the form
     *                (1,w1,€p1)(2,w2,€p2)...(m,wm,€pm)
     * @return An instance of {@link Matcher}, matching each triple
     * @throws LineFormatException If the triples are malformed
     */
//    @NotNull
    private Matcher getMatcher(int lineNo, String triples) throws LineFormatException {
        if (!ALL_ITEMS_PATTERN.matcher(triples).matches())
            throw new LineFormatException(
                    lineNo, "Items must be separated by matching pairs of parentheses");

        return SINGLE_ITEM_PATTERN.matcher(triples);
    }

    /**
     * A line must have the form
     * <p>a : b</p>
     * where
     * <ul>
     * <li> a is the maximum weight of the package, and</li>
     * <li> b is a representation of items.</li>
     * </ul>
     * <p>
     * This method splits the line using ":" into two sections:
     * <ul>
     * <li>section[0] = a</li>
     * <li>section[1] = b</li>
     * </ul>
     * It checks there are exactly two sections, and
     * section[0] is a valid real number
     * </p>
     *
     * @param lineNo Line number, used to give meaningful errors
     * @param line   The line itself
     * @return sections array, as explained above
     * @throws LineFormatException If the line is malformed
     */
//    @NotNull
    private String[] getSections(final int lineNo, final String line) throws LineFormatException {
        if (line == null) throw new LineFormatException(lineNo, "Line cannot be null");

        String strippedLine = line.replaceAll("\\s+", "");
        if (strippedLine.isEmpty()) throw new LineFormatException(lineNo, "Line cannot be blank");

        String[] sections = strippedLine.split(":");
        if (sections.length != 2)
            throw new LineFormatException(lineNo, "The line is not in a:b format");

        if (!DECIMAL_PATTERN.matcher(sections[0]).matches())
            throw new LineFormatException(lineNo, "Maximum weight must be a positive number");

        return sections;
    }

    /**
     * @return An unmodifiable view of {@code map}, to preserve immutability
     */
    public Map<Integer, Item> getMap() {
        return Collections.unmodifiableMap(map);
    }

    /**
     * @return An unmodifiable view of {@code items}, to preserve immutability
     */
    public List<Item> getItems() {
        return Collections.unmodifiableList(items);
    }
}
