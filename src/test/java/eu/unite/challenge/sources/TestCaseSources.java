package eu.unite.challenge.sources;

import net.jcip.annotations.Immutable;
import org.junit.jupiter.params.provider.Arguments;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/*
 * The static members could have been defined as Stream<List>,
 * but that would make them mutable: Once a stream is traversed,
 * it could not be re-used by multiple tests withing a single run.
 *
 * Instead, we used Unmodifiable Lists with immutable elements.
 */

@Immutable
public class TestCaseSources {
    private final static SecureRandom r = new SecureRandom();

    private TestCaseSources() {
    }

    /*
     * List.of() does not support null, so Arrays.asList is used.
     * The latter is not unmodifiable, so it was wrapped within one.
     */
    public static final List<String> BAD_ITEMS = Collections.unmodifiableList(Arrays.asList(
            null,
            "",
            "       ",
            "1,2",
            "1,2.2",
            "1,2,3",
            "1,0,3",
            "-1,2,3",
            "1,-2,3",
            "1,2,-3",
            "1, 2.2.2, €3",
            "100,2,€3",
            "x,1,2",
            "1,x,2",
            "1,2,x"
    ));

    public static final List<String> GIVEN_SOLUTION = List.of(
            "4",
            "-",
            "2,7",
            "8,9"
    );
    public static final List<String> GIVEN_TEST_CASE = List.of(
            "81 : (1,53.38,€45) (2,88.62,€98) (3,78.48,€3) (4,72.30,€76) (5,30.18,€9) (6,46.34,€48)",
            "8 : (1,15.3,€34)",
            "75 : (1,85.31,€29) (2,14.55,€74) (3,3.98,€16) (4,26.24,€55) (5,63.69,€52) (6,76.25,€75) (7,60.02,€74) (8,93.18,€35) (9,89.95,€78)",
            "56 : (1,90.72,€13) (2,33.80,€40) (3,43.15,€10) (4,37.97,€16) (5,46.81,€36) (6,48.77,€79) (7,81.80,€45) (8,19.36,€79) (9,6.76,€64)"
    );

    public static Stream<Arguments> badItems() {
        return IntStream.range(0, BAD_ITEMS.size())
                .mapToObj(
                        i -> Arguments.arguments(i + 1, BAD_ITEMS.get(i))
                );
    }

    public static List<Arguments> givenTestCase() {
        return IntStream.range(0, GIVEN_TEST_CASE.size())
                .mapToObj(
                        i -> Arguments.arguments(i + 1, GIVEN_TEST_CASE.get(i), GIVEN_SOLUTION.get(i))
                ).collect(Collectors.toUnmodifiableList());
    }

    public static Stream<Arguments> randomSource() {
        Arguments[] args = new Arguments[1000];

        for (int i = 0; i < args.length; i++) {
            StringBuilder sb = new StringBuilder();
            sb.append(randomDecimal(100, 1))
                    .append(":");
            for (int j = 0; j <= r.nextInt(5); j++) {
                BigDecimal w = randomDecimal(70, 2);
                BigDecimal p = randomDecimal(100, 3);
                sb.append(formatItem(j, w, p));

                // add item with the same weight but different price
                BigDecimal p1 = randomDecimal(100, 3);
                sb.append(formatItem(++j, w, p1));

                // add item with the same price but different weight
                BigDecimal w1 = randomDecimal(70, 2);
                sb.append(formatItem(++j, w1, p));
            }
            args[i] = Arguments.arguments(i + 1, sb.toString());
        }

        return Arrays.stream(args);
    }

    private static StringBuilder formatItem(int i, BigDecimal w, BigDecimal p) {
        StringBuilder sb = new StringBuilder();
        sb.append("(")
                .append(i + 1)
                .append(",")
                .append(w.toPlainString())
                .append(",€")
                .append(p)
                .append(")");
        return sb;
    }

    static BigDecimal randomDecimal(final int bound, final int scale) {
        BigDecimal d = BigDecimal.valueOf(r.nextDouble() * bound)
                .setScale(scale, RoundingMode.HALF_UP);
        if (d.compareTo(BigDecimal.ZERO) <= 0)
            d = BigDecimal.ONE;
        return d;
    }
}
