package eu.unite.challenge.dataobjects;

import eu.unite.challenge.exceptions.ItemException;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class ItemTest {
    private static final Logger logger = LoggerFactory.getLogger(ItemTest.class);

    Random r = new Random();

    @ParameterizedTest
    @MethodSource("eu.unite.challenge.sources.TestCaseSources#badItems")
    void checkForThrows1(final int index, final String s) {
        ItemException e = assertThrows(ItemException.class, () -> new Item(index, s));
        logger.trace("{}", e.getMessage());
    }

    @ParameterizedTest
    @CsvSource({"-1,2,3", "1,-2,3", "1,2,-3", "1,2,-3", "1,2000,3", "1,2,3000", "100,100,100"})
    void checkForThrows2(final int number, final BigDecimal weight, final BigDecimal price) {
        ItemException e = assertThrows(ItemException.class, () -> new Item(number, weight, price));
        logger.trace("{}", e.getMessage());
    }


    @ParameterizedTest
    @ValueSource(strings = {"1,2,€3", " 1 , 2 , € 3", "1,2.2,€3", "1,2.2,€3.3", "1,0.2,€3", "1,.2,€3"})
    void checkForNotThrows1(String s) {
        assertDoesNotThrow(() -> new Item(1, s));
    }

    @ParameterizedTest
    @CsvSource({"1,2.2,3.65", "10,100,100"})
    void checkForNotThrows2(final int number, final BigDecimal weight, final BigDecimal price) {
        Item item = assertDoesNotThrow(() -> new Item(number, weight, price));
        assertEquals(
                String.format("(%d, %s, €%s)", number, weight.toPlainString(), price.toPlainString()),
                item.toString());
        assertEquals(Objects.hash(number, weight, price),
                item.hashCode());
    }

    @RepeatedTest(100)
    void checkForEquality() {
        int number = r.nextInt(15) + 1;
        BigDecimal weight = BigDecimal.valueOf(r.nextDouble() * 10 + 0.1);
        BigDecimal cost = BigDecimal.valueOf(r.nextDouble() * 10 + 0.1);

        Item item1 = assertDoesNotThrow(() -> new Item(number, weight, cost));
        String triple = item1.tripleString();
        logger.trace("{}", triple);

        Item item2 = assertDoesNotThrow(() -> new Item(number, triple));
        assertEquals(item1, item2);
    }
}