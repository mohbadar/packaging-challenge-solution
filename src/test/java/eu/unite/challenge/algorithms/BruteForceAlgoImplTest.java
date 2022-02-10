package eu.unite.challenge.algorithms;

import eu.unite.challenge.dataobjects.RecordInstance;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class BruteForceAlgoImplTest {

    @ParameterizedTest
    @MethodSource("eu.unite.challenge.sources.TestCaseSources#givenTestCase")
    void solve(final int lineNo, final String line, final String solution) throws Exception {
        BruteForceAlgoImpl bruteForce = new BruteForceAlgoImpl(new RecordInstance(lineNo, line));
        assertEquals(bruteForce.getBag().getResult(), solution);
    }

    @Test
    void nullTest() {
        BruteForceAlgoImpl bruteForce = new BruteForceAlgoImpl(null);
        assertEquals("ERR", bruteForce.toString());
    }
}
