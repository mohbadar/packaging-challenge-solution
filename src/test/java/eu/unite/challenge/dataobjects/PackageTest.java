package eu.unite.challenge.dataobjects;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PackageTest {

    @Test
    public void testPackage() {
        // If map is null, NPE is thrown
        assertThrows(NullPointerException.class, () -> new Package(null, null));

        Map<Integer, Item> map = new HashMap<>();
        // sortedLabels can null, map can be empty
        assertDoesNotThrow(() -> new Package(map, null));

        map.put(1, null);
        // sortedLabels can null, map can be non-empty
        assertDoesNotThrow(() -> new Package(map, null));

        SortedSet<Integer> sortedLabels = new TreeSet<>();
        // sortedLabels can be empty
        assertDoesNotThrow(() -> new Package(map, sortedLabels));

        sortedLabels.add(5);
        // sortedLabels must be a subset of map key-set
        assertThrows(IllegalArgumentException.class, () -> new Package(map, sortedLabels));

        map.put(5, null);
        // map contains null entries, NPE is thrown
        assertThrows(NullPointerException.class, () -> new Package(map, sortedLabels));

        map.clear();
        // sortedLabels must be a subset of map key-set
        assertThrows(IllegalArgumentException.class, () -> new Package(map, sortedLabels));

        // wight and price for the 5th item
        BigDecimal w5 = BigDecimal.valueOf(8.36);
        BigDecimal p5 = BigDecimal.valueOf(5.12);

        assertDoesNotThrow(() -> map.put(5, new Item(5, w5, p5)));

        Package bag = assertDoesNotThrow(() -> new Package(map, sortedLabels));
        assertEquals(sortedLabels, bag.getIndices());
        assertEquals(p5, bag.getResultPrice());
        assertEquals(w5, bag.getResultWeight());
        assertEquals("5", bag.getResult());
        assertEquals(String.format("Price = %s, weight = %s, result = (%s).",
                p5.toPlainString(), w5.toPlainString(), "5"), bag.toString());

        sortedLabels.add(8);
        // wight and price for the 8th item
        BigDecimal w8 = BigDecimal.valueOf(17.698);
        BigDecimal p8 = BigDecimal.valueOf(99.0);

        assertDoesNotThrow(() -> map.put(8, new Item(8, w8, p8)));
        bag = new Package(map, sortedLabels);
        assertEquals(sortedLabels, bag.getIndices());
        assertEquals(p5.add(p8), bag.getResultPrice());
        assertEquals(w5.add(w8), bag.getResultWeight());
        assertEquals("5,8", bag.getResult());
    }

}