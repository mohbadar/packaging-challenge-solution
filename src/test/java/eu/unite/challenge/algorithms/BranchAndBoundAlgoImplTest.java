package eu.unite.challenge.algorithms;


import eu.unite.challenge.dataobjects.Package;
import eu.unite.challenge.dataobjects.RecordInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

class BranchAndBoundAlgoImplTest {
    private static final Logger logger = LoggerFactory.getLogger(BranchAndBoundAlgoImplTest.class);

    @ParameterizedTest
    @MethodSource("eu.unite.challenge.sources.TestCaseSources#randomSource")
    void randomCase(final int lineNo, final String line) {
        logger.trace("{} --- {}\n", lineNo, line);

        RecordInstance p = assertDoesNotThrow(() -> new RecordInstance(lineNo, line));
        BruteForceAlgoImpl bf = new BruteForceAlgoImpl(p);
        BranchAndBoundAlgoImpl bb = new BranchAndBoundAlgoImpl(p);

        Package bfBag = bf.getBag();
        Package bbBag = bb.getBag();

        logger.trace("Brute force result:      " + bfBag);
        logger.trace("Branch & bound result:   " + bbBag);

        BigDecimal bfPrice = bfBag.getResultPrice();
        BigDecimal bbPrice = bbBag.getResultPrice();

        assertEquals(bfPrice, bbPrice);
    }
}