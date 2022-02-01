package eu.unite.challenge.algorithms;

import eu.unite.challenge.dataobjects.RecordInstance;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BruteForceTest {

    @ParameterizedTest
    @MethodSource("eu.unite.challenge.sources.TestCaseSources#givenTestCase")
    void solve(final int lineNo, final String line, final String solution) throws Exception {
        BruteForce bruteForce = new BruteForce(new RecordInstance(lineNo, line));
        assertEquals(bruteForce.getBag().getResult(), solution);
    }

    @Test
    void nullTest() {
        BruteForce bruteForce = new BruteForce(null);
        assertEquals("ERR", bruteForce.toString());
    }
}
