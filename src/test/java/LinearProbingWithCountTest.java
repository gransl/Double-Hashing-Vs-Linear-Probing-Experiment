import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


class LinearProbingWithCountTest {

    /**
     * I learned how to test print methods in this way from this StackOverflow post:
     * https://stackoverflow.com/questions/32241057/how-to-test-a-print-method-in-java-using-junit
     */
    @Test
    void displayHashTable() {
        LinearProbingWithCount<String, Double> mathConstants = new LinearProbingWithCount<>();
        mathConstants.add("pi", 3.1415);
        mathConstants.add("e", 2.718);
        mathConstants.add("tau", 6.28);

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        mathConstants.displayHashTable();

        String expectedOutput = "null \nnull \n" + "pi 3.1415\n" + "e 2.718\n" + "tau 6.28\n" + "null \n" +
                "null \n" +
                "null \n" +
                "null \n" +
                "null \n" +
                "null \n\n";
        assertEquals(expectedOutput, outContent.toString());
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
    void remove() {
        LinearProbingWithCount<String, Double> mathConstants = new LinearProbingWithCount<>();
        mathConstants.add("e", 2.718);
        assertEquals(2.718, mathConstants.remove("e"));
        assertFalse(mathConstants.contains("e"));
        assertNull(mathConstants.remove("phi"));
    }

    @Test
    void getValueAndContains() {
        LinearProbingWithCount<String, Double> mathConstants = new LinearProbingWithCount<>();
        mathConstants.add("phi", 1.6180);
        assertEquals(1.6180, mathConstants.getValue("phi"));
        assertNull(mathConstants.getValue("pi"));
    }

    @Test
    void containsAndProbeCount() {
        LinearProbingWithCount<Integer, String> lp3 = new LinearProbingWithCount<>();
        assertEquals(0, lp3.getProbeCount());
        lp3.add(2, "two");
        lp3.resetProbeCount();
        assertTrue(lp3.contains(2));
        assertEquals(1, lp3.getProbeCount());
        assertFalse(lp3.contains(13));
        assertEquals(3, lp3.getProbeCount());
        assertFalse(lp3.contains(4));
        assertEquals(4, lp3.getProbeCount());
        lp3.resetProbeCount();
        assertEquals(0, lp3.getProbeCount());
    }

    @Test
    void isEmpty() {
        LinearProbingWithCount<String, String> emptyDictionary = new LinearProbingWithCount<>();
        assertTrue(emptyDictionary.isEmpty());
        emptyDictionary.add("thunder", "lightning");
        assertFalse(emptyDictionary.isEmpty());
    }

    @Test
    void getSize() {
        LinearProbingWithCount<String, Double> mathConstants = new LinearProbingWithCount<>();
        assertEquals(0, mathConstants.getSize());
        mathConstants.add("pi", 3.1415);
        mathConstants.add("e", 2.718);
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
        mathConstants.add("pi", 3.1415);
        mathConstants.add("e", 2.718);
        mathConstants.add("tau", 6.28);
        mathConstants.add("phi", 1.6180);
        mathConstants.add("Catalan", 0.91596);
        assertEquals(5, mathConstants.getSize());
        mathConstants.clear();
        assertEquals(0, mathConstants.getSize());
    }

    @Test
    void getKeyIterator() {
        LinearProbingWithCount<Integer, String> lp4 = new LinearProbingWithCount<>();
        lp4.add(2, "two");
        lp4.add(5, "five");
        lp4.add(1, "one");
        lp4.add(3, "three");
        lp4.add(7, "seven");
        lp4.add(10, "ten");
        lp4.remove(5);
        Iterator<Integer> mcIterator = lp4.getKeyIterator();
        assertTrue(mcIterator.hasNext());
        assertEquals(1, mcIterator.next());
        assertEquals(2, mcIterator.next());
        assertEquals(3, mcIterator.next());
        assertThrows(UnsupportedOperationException.class, () -> mcIterator.remove());
        assertEquals(7, mcIterator.next());
        assertEquals(10, mcIterator.next());
        assertThrows(NoSuchElementException.class, () -> mcIterator.next());
        assertFalse(mcIterator.hasNext());
    }

    @Test
    void getValueIterator() {
        LinearProbingWithCount<Integer, String> lp5 = new LinearProbingWithCount<>();
        lp5.add(2, "two");
        lp5.add(5, "five");
        lp5.add(1, "one");
        lp5.add(3, "three");
        lp5.add(7, "seven");
        lp5.add(10, "ten");
        lp5.remove(5);
        Iterator<String> mcIterator = lp5.getValueIterator();
        assertTrue(mcIterator.hasNext());
        assertEquals("one", mcIterator.next());
        assertEquals("two", mcIterator.next());
        assertEquals("three", mcIterator.next());
        assertThrows(UnsupportedOperationException.class, () -> mcIterator.remove());
        assertEquals("seven", mcIterator.next());
        assertEquals("ten", mcIterator.next());
        assertThrows(NoSuchElementException.class, () -> mcIterator.next());
        assertFalse(mcIterator.hasNext());
    }

    @Test
    void getLoadFactor() {
        LinearProbingWithCount<Integer, String> lp6 = new LinearProbingWithCount<>();
        lp6.add(2, "two");
        lp6.add(5, "five");
        lp6.add(1, "one");
        lp6.add(3, "three");
        lp6.add(7, "seven");
        double expectedValue = 5.0/11.0;
        assertEquals(expectedValue, lp6.getLoadFactor());
    }

    @Test
    void getHashTableSize() {
        LinearProbingWithCount<Integer, String> lp7 = new LinearProbingWithCount<>();
        assertEquals(11, lp7.getHashTableSize());
    }
}