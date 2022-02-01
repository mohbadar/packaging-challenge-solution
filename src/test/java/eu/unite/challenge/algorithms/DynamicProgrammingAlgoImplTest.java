package eu.unite.challenge.algorithms;


import eu.unite.challenge.dataobjects.Item;
import eu.unite.challenge.dataobjects.Package;
import eu.unite.challenge.dataobjects.RecordInstance;
import eu.unite.challenge.exceptions.ItemException;
import eu.unite.challenge.exceptions.LineFormatException;
import eu.unite.challenge.exceptions.OutOfRangeProblemSizeException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.SortedSet;

import static org.junit.jupiter.api.Assertions.*;

class DynamicProgrammingAlgoImplTest {
    private static final Logger logger = LoggerFactory.getLogger(DynamicProgrammingAlgoImpl.class);

    static Random r = new Random();

    @Test
    void testDpSolve() throws ItemException {
        List<Item> items = List.of(
                new Item(1, "1, 5.3, €10"),
                new Item(2, "2, 4,   €40"),
                new Item(3, "3, 6.7, €30"),
                new Item(4, "4, 3.5, €50")
        );

        SortedSet<Integer> indices = DynamicProgrammingAlgoImpl.dpSolve(BigDecimal.valueOf(10), 1, items);
        assertEquals(List.of(2, 4), new ArrayList<>(indices));
    }

    @Test
    void testDpSolveEdgeCase() throws ItemException {
        List<Item> items = List.of(
                new Item(1, "1, 6.1, €10"),
                new Item(2, "2, 7, €10"),
                new Item(3, "3, 5, €10"),
                new Item(4, "4, 3, €50")
        );

        SortedSet<Integer> indices = DynamicProgrammingAlgoImpl.dpSolve(BigDecimal.valueOf(10), 1, items);
        assertEquals(List.of(3, 4), new ArrayList<>(indices));
    }

    @Test
    void testHuge() throws LineFormatException {
        RecordInstance p = new RecordInstance(1, "99.8135: (1, 5.33, €10)");
        assertThrows(OutOfRangeProblemSizeException.class, () ->
                new DynamicProgrammingAlgoImpl(p));
    }

    @ParameterizedTest
    @MethodSource("eu.unite.challenge.sources.TestCaseSources#randomSource")
    void randomCase(final int lineNo, final String line) {
        logger.trace("{} --- {}\n", lineNo, line);

        RecordInstance p = assertDoesNotThrow(() -> new RecordInstance(lineNo, line));
        BruteForceAlgoImpl bf = new BruteForceAlgoImpl(p);
        DynamicProgrammingAlgoImpl dp = new DynamicProgrammingAlgoImpl(p);

        Package bfBag = bf.getBag();
        Package dpBag = dp.getBag();

        logger.trace("Brute force result:      " + bfBag);
        logger.trace("Dynamic programming result:   " + dpBag);

        BigDecimal bfPrice = bfBag.getResultPrice();
        BigDecimal dpPrice = dpBag.getResultPrice();

        assertEquals(bfPrice, dpPrice);
    }
}