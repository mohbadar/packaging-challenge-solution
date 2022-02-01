package eu.unite.challenge.dataobjects;


import eu.unite.challenge.exceptions.FormatException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static eu.unite.challenge.validations.ConstraintRules.MAX_ITEMS_PER_LINE;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RecordInstanceTest {
    private static final transient Logger logger = LoggerFactory.getLogger(RecordInstanceTest.class);

    @ParameterizedTest
    @MethodSource("eu.unite.challenge.sources.TestCaseSources#badItems")
    void checkBadInstances(final int index, final String line) {
        assertThrows(FormatException.class, ()
                -> new RecordInstance(index, "100:(" + line + ")"));
    }

    @Test
    void checkEdgeCases() {
        Exception ex;
        ex = assertThrows(FormatException.class, () -> new RecordInstance(0, null));
        logger.trace("{}", ex.getMessage());
        ex = assertThrows(FormatException.class, () -> new RecordInstance(0, ""));
        logger.trace("{}", ex.getMessage());
        ex = assertThrows(FormatException.class, () -> new RecordInstance(0, "1000:(1,1,€2)"));
        logger.trace("{}", ex.getMessage());
        ex = assertThrows(FormatException.class, () -> new RecordInstance(0, "X:(1,1,€2)"));
        logger.trace("{}", ex.getMessage());
        ex = assertThrows(FormatException.class, () -> new RecordInstance(0, "100:(0,1,€2)"));
        logger.trace("{}", ex.getMessage());
        ex = assertThrows(FormatException.class, () -> new RecordInstance(0, "100:(1,1,€2"));
        logger.trace("{}", ex.getMessage());

        StringBuilder sb = new StringBuilder("100:");
        for (int i = 1; i <= MAX_ITEMS_PER_LINE + 1; i++) {
            sb.append("(")
                    .append(i)
                    .append(",2,€3)");
        }

        ex = assertThrows(FormatException.class, () -> new RecordInstance(0, sb.toString()));
        logger.trace("{}", ex.getMessage());
    }

    @ParameterizedTest
    @MethodSource("eu.unite.challenge.sources.TestCaseSources#givenTestCase")
    void checkValidLine(final int lineNo, final String line) {
        assertDoesNotThrow(() -> new RecordInstance(lineNo, line));
    }
}
