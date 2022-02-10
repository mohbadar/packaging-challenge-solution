package eu.unite.challenge.algorithms;


import eu.unite.challenge.dataobjects.Package;
import eu.unite.challenge.dataobjects.RecordInstance;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class GreedyApproximationAlgoImplTest {
    private static final Logger logger = LoggerFactory.getLogger(GreedyApproximationAlgoImplTest.class);


    static final BigDecimal TWO = BigDecimal.valueOf(2);

    @ParameterizedTest
    @MethodSource("eu.unite.challenge.sources.TestCaseSources#givenTestCase")
    void greedySolve(final int lineNo, final String line, final String solution) {
        RecordInstance p = assertDoesNotThrow(() -> new RecordInstance(lineNo, line));

        SortedSet<Integer> sol;
        if (solution.equals("-"))
            sol = null;
        else
            sol = Arrays.stream(solution.split("\\s*,\\s*"))
                    .map(Integer::parseInt)
                    .collect(Collectors.toCollection(TreeSet::new));

        Package bag = new Package(p.getMap(), sol);

        GreedyApproximationAlgoImpl ga = new GreedyApproximationAlgoImpl(p);

        if (sol != null)
            assertTrue(ga.getBag().getResultPrice().compareTo(bag.getResultPrice().multiply(TWO)) < 0);


        logger.trace("Price:  approx. {} vs real {}\n", ga.getBag().getResultPrice(), bag.getResultPrice());
        logger.trace("Weight: approx. {} vs real {}\n", ga.getBag().getResultWeight(), bag.getResultWeight());
    }

    @Test
    void greedyEdgeCase() throws Exception {
        RecordInstance p = new RecordInstance(1, "10 : (1, 1, €1) (2, 10, €9)");
        GreedyApproximationAlgoImpl ga = new GreedyApproximationAlgoImpl(p);
        assertEquals(BigDecimal.valueOf(9), ga.getBag().getResultPrice());
        logger.trace(ga.getBag().toString());
    }

    @ParameterizedTest
    @MethodSource("eu.unite.challenge.sources.TestCaseSources#randomSource")
    void randomCase(final int lineNo, final String line) {
        logger.trace("{} --- {}\n", lineNo, line);

        RecordInstance p = assertDoesNotThrow(() -> new RecordInstance(lineNo, line));
        BruteForceAlgoImpl bf = new BruteForceAlgoImpl(p);
        GreedyApproximationAlgoImpl ga = new GreedyApproximationAlgoImpl(p);

        Package bfBag = bf.getBag();
        Package gaBag = ga.getBag();

        logger.trace("Brute force result:      " + bfBag);
        logger.trace("Greedy approx. result:   " + gaBag);

        BigDecimal bfPrice = bfBag.getResultPrice();
        BigDecimal gaPrice = gaBag.getResultPrice();

        if (!bfPrice.equals(BigDecimal.ZERO))
            assertTrue(bfPrice.compareTo(gaPrice.multiply(TWO)) < 0);
    }
}