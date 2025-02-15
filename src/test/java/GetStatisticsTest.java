import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;


class GetStatisticsTest {

    @Test
    void calculateLinearCapacity() {
        GetStatistics testStat = new GetStatistics(false);
        assertEquals(341, testStat.calculateLinearCapacity(100, 1.5), 1);
    }

    @Test
    void calculateDoubleCapacity() {
        GetStatistics testStat = new GetStatistics(false);
        assertEquals(300, testStat.calculateDoubleCapacity(100, 1.5), 1);
    }

    @Test
    void createDisjointNameArrays() {
        GetStatistics testStat =  new GetStatistics(false);
        String[] addNames = testStat.getAddNames();
        String[] searchNames = testStat.getSearchNames();
        HashSet<String> testAddNames = new HashSet<>();
        HashSet<String> testSearchNames = new HashSet<>();
        HashSet<String> allNames = new HashSet<>();

        for (String name : addNames){
            testAddNames.add(name);
            allNames.add(name);
        }

        for (String name : searchNames) {
            testSearchNames.add(name);
            allNames.add(name);
        }

        assertEquals(addNames.length, testAddNames.size());
        assertEquals(searchNames.length, testSearchNames.size());
        assertEquals(11000, allNames.size());
    }

    @Test
    void createDisjointNameArrays2() {
        GetStatistics testStat =  new GetStatistics(true);
        String[] addNames = testStat.getAddNames();
        String[] searchNames = testStat.getSearchNames();
        HashSet<String> testAddNames = new HashSet<>();
        HashSet<String> testSearchNames = new HashSet<>();
        HashSet<String> allNames = new HashSet<>();

        for (String name : addNames){
            testAddNames.add(name);
            allNames.add(name);
        }

        for (String name : searchNames) {
            testSearchNames.add(name);
            allNames.add(name);
        }

        assertEquals(addNames.length, testAddNames.size());
        assertEquals(searchNames.length, testSearchNames.size());
        assertEquals(11000, allNames.size());
    }

    @Test
    void runExperiment() {

    }

    @Test
    void generateNUniqueIntegers() {
        GetStatistics testStat = new GetStatistics(false);
        ArrayList<Integer> testSet = testStat.generateNUniqueIntegers(5,11);
        assertEquals(5, testSet.size());
        for (int num: testSet) {
            assertEquals(5, num, 5);
        }

    }

    @Test
    void computeAverageOfArray() {
        GetStatistics testStat = new GetStatistics(false);
        int[] testArray = {2,4,4,4,5,5,7,9};
        assertEquals(5, testStat.computeAverageOfArray(testArray));
    }

    @Test
    void computeStandardDeviation() {
        GetStatistics testStat = new GetStatistics(false);
        int[] testArray = {2,4,4,4,5,5,7,9};
        double expected = Math.sqrt(32.0/7.0);
        assertEquals(expected, testStat.computeStandardDeviation(testArray));
    }
}