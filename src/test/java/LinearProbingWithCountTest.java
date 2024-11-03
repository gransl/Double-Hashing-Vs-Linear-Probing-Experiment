import org.junit.jupiter.api.Test;

import java.util.Iterator;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


class LinearProbingWithCountTest {

    @Test
    void defaultConstructor() {


    }

    @Test
    void constructor() {

    }

    @Test
    void displayHashTable() {
    }

    @Test
    void add() {
        LinearProbingWithCount<String, String> lp1 = new LinearProbingWithCount<>();
        String str = lp1.add("hello", "goodbye");
        assertNull(str);
        String str2 = lp1.add("hello", "farewell");
        assertEquals(str2, "goodbye");
        lp1.add("str3", "what");
        lp1.add("str4", "how");
        lp1.add("str5", "who");
        assertThrows(IllegalArgumentException.class, () -> lp1.add(null,"error!"));
        assertThrows(IllegalArgumentException.class, () -> lp1.add("error!", null));

    }

    @Test
    void addWithCount() {
        LinearProbingWithCount<Integer, String> lp2 = new LinearProbingWithCount<>();
        int count = lp2.addWithCount(5, "hooray");
        assertEquals(0, count);
        int count2 = lp2.addWithCount(10, "tortoise");
        assertEquals(1, count2);
        lp2.addWithCount(100, "what");
        lp2.addWithCount(202, "how");
        lp2.addWithCount(314, "who");
        assertThrows(IllegalArgumentException.class, () -> lp2.addWithCount(null,"error!"));
        assertThrows(IllegalArgumentException.class, () -> lp2.addWithCount(3, null));
    }

    @Test
    void removeAndAddAtRemovedIndex() {
        LinearProbingWithCount<String, Double> mathConstants = new LinearProbingWithCount<>();
        mathConstants.addWithCount("pi", 3.1415);
        mathConstants.addWithCount("e", 2.718);
        assertEquals(3.1415, mathConstants.remove("pi"));
        assertEquals(2.718, mathConstants.remove("e"));
        assertNull(mathConstants.add("tau", 6.28));
        assertEquals(0, mathConstants.addWithCount("e", 2.718));
        assertNull(mathConstants.remove("phi"));
    }

    @Test
    void getValueAndContains() {
        LinearProbingWithCount<String, Double> mathConstants = new LinearProbingWithCount<>();
        mathConstants.addWithCount("phi", 1.6180);
        assertEquals(1.6180, mathConstants.getValue("phi"));
        assertNull(mathConstants.getValue("pi"));
    }

    @Test
    void contains() {
        LinearProbingWithCount<String, Double> mathConstants = new LinearProbingWithCount<>();
        mathConstants.addWithCount("phi", 1.6180);
        assertTrue(mathConstants.contains("phi"));
        assertFalse(mathConstants.contains("pi"));
    }


    @Test
    void isEmpty() {
        LinearProbingWithCount<String, String> emptyDictionary = new LinearProbingWithCount<>();
        assertTrue(emptyDictionary.isEmpty());
        emptyDictionary.addWithCount("thunder", "lightning");
        assertFalse(emptyDictionary.isEmpty());
    }

    @Test
    void getSize() {
        LinearProbingWithCount<String, Double> mathConstants = new LinearProbingWithCount<>();
        assertEquals(0, mathConstants.getSize());
        mathConstants.addWithCount("pi", 3.1415);
        mathConstants.addWithCount("e", 2.718);
        assertEquals(2, mathConstants.getSize());
        mathConstants.add("tau", 6.28);
        mathConstants.add("phi", 1.6180);
        mathConstants.add("Catalan", 0.91596);
        assertEquals(5, mathConstants.getSize());
        mathConstants.remove("pi");
        assertEquals(4, mathConstants.getSize());
    }

    @Test
    void clear() {
        LinearProbingWithCount<String, Double> mathConstants = new LinearProbingWithCount<>();
        mathConstants.addWithCount("pi", 3.1415);
        mathConstants.addWithCount("e", 2.718);
        mathConstants.add("tau", 6.28);
        mathConstants.add("phi", 1.6180);
        mathConstants.add("Catalan", 0.91596);
        assertEquals(5, mathConstants.getSize());
        mathConstants.clear();
        assertEquals(0, mathConstants.getSize());
    }

    @Test
    void getKeyIterator() {
        LinearProbingWithCount<String, Double> mathConstants = new LinearProbingWithCount<>();
        mathConstants.addWithCount("pi", 3.1415);
        mathConstants.addWithCount("e", 2.718);
        mathConstants.add("tau", 6.28);
        mathConstants.add("phi", 1.6180);
        mathConstants.addWithCount("Catalan", 0.91596);
        mathConstants.addWithCount("Fibonacci", 3.3599);
        mathConstants.remove("Fibonacci");
        Iterator<String> mcIterator = mathConstants.getKeyIterator();
        assertTrue(mcIterator.hasNext());
        assertEquals("e", mcIterator.next());
        assertEquals("phi", mcIterator.next());
        assertEquals("pi", mcIterator.next());
        assertThrows(UnsupportedOperationException.class, () -> mcIterator.remove());
        assertEquals("tau", mcIterator.next());
        assertEquals("Catalan", mcIterator.next());
        assertThrows(NoSuchElementException.class, () -> mcIterator.next());
        assertFalse(mcIterator.hasNext());
    }

    @Test
    void getValueIterator() {
        LinearProbingWithCount<String, Double> mathConstants = new LinearProbingWithCount<>();
        mathConstants.addWithCount("pi", 3.1415);
        mathConstants.addWithCount("e", 2.718);
        mathConstants.add("tau", 6.28);
        mathConstants.add("phi", 1.6180);
        mathConstants.addWithCount("Catalan", 0.91596);
        mathConstants.addWithCount("Fibonacci", 3.3599);
        mathConstants.remove("Fibonacci");
        Iterator<Double> mcIterator = mathConstants.getValueIterator();
        assertTrue(mcIterator.hasNext());
        assertEquals(2.718, mcIterator.next());
        assertEquals(1.6180, mcIterator.next());
        assertEquals(3.1415, mcIterator.next());
        assertThrows(UnsupportedOperationException.class, () -> mcIterator.remove());
        assertEquals(6.28, mcIterator.next());
        assertEquals(0.91596, mcIterator.next());
        assertThrows(NoSuchElementException.class, () -> mcIterator.next());
        assertFalse(mcIterator.hasNext());
    }

}