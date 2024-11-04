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


class DoubleHashingWithCountTest {

    /**
     * I learned how to test print methods in this way from this StackOverflow post:
     * https://stackoverflow.com/questions/32241057/how-to-test-a-print-method-in-java-using-junit
     */
    @Test
    void displayHashTable() {
        DoubleHashingWithCount<String, Double> mathConstants = new DoubleHashingWithCount<>();
        mathConstants.add("pi", 3.1415);
        mathConstants.add("e", 2.718);
        mathConstants.add("tau", 6.28);

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        mathConstants.displayHashTable();

        String expectedOutput = "null \n" + "tau 6.28\n" + "pi 3.1415\n" + "null \n" +
                "null \n" +
                "null \n" +
                "null \n" +
                "e 2.718\n" +
                "null \n" +
                "null \n" +
                "null \n\n";
        assertEquals(expectedOutput, outContent.toString());
    }

    @Test
    void add() {
        DoubleHashingWithCount<String, String> lp1 = new DoubleHashingWithCount<>();
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
        DoubleHashingWithCount<String, Double> mathConstants = new DoubleHashingWithCount<>();
        mathConstants.add("e", 2.718);
        assertEquals(2.718, mathConstants.remove("e"));
        assertFalse(mathConstants.contains("e"));
        assertNull(mathConstants.remove("phi"));
    }

    @Test
    void getValueAndContains() {
        DoubleHashingWithCount<String, Double> mathConstants = new DoubleHashingWithCount<>();
        mathConstants.add("phi", 1.6180);
        assertEquals(1.6180, mathConstants.getValue("phi"));
        assertNull(mathConstants.getValue("pi"));
    }

    @Test
    void containsAndProbeCount() {
        DoubleHashingWithCount<Integer, String> dh3 = new DoubleHashingWithCount<>();
        assertEquals(0, dh3.getProbeCount());
        dh3.add(2, "two");
        dh3.resetProbeCount();
        assertTrue(dh3.contains(2));
        assertEquals(1, dh3.getProbeCount());
        assertFalse(dh3.contains(13));
        assertEquals(3, dh3.getProbeCount());
        assertFalse(dh3.contains(4));
        assertEquals(4, dh3.getProbeCount());
        dh3.resetProbeCount();
        assertEquals(0, dh3.getProbeCount());
    }


    @Test
    void isEmpty() {
        DoubleHashingWithCount<String, String> emptyDictionary = new DoubleHashingWithCount<>();
        assertTrue(emptyDictionary.isEmpty());
        emptyDictionary.add("thunder", "lightning");
        assertFalse(emptyDictionary.isEmpty());
    }

    @Test
    void getSize() {
        DoubleHashingWithCount<String, Double> mathConstants = new DoubleHashingWithCount<>();
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
        DoubleHashingWithCount<String, Double> mathConstants = new DoubleHashingWithCount<>();
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
        DoubleHashingWithCount<Integer, String> dh4 = new DoubleHashingWithCount<>();
        dh4.add(2, "two");
        dh4.add(5, "five");
        dh4.add(1, "one");
        dh4.add(3, "three");
        dh4.add(7, "seven");
        dh4.add(10, "ten");
        dh4.remove(5);
        Iterator<Integer> mcIterator = dh4.getKeyIterator();
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
        DoubleHashingWithCount<Integer, String> dh5 = new DoubleHashingWithCount<>();
        dh5.add(2, "two");
        dh5.add(5, "five");
        dh5.add(1, "one");
        dh5.add(3, "three");
        dh5.add(7, "seven");
        dh5.add(10, "ten");
        dh5.remove(5);
        Iterator<String> mcIterator = dh5.getValueIterator();
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

}