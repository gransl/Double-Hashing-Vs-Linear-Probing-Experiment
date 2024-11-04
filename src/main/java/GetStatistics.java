import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Random;

public class GetStatistics {
    private final LinearProbingWithCount<String, String> linearHash;
    private final DoubleHashingWithCount<String, String> doubleHash;
    private final String[] addNames;
    private final String[] searchNames;
    private int[] linearCollisions;
    private int[] doubleCollisions;



    /**
     * sets up basic experiment of 100 names for 1.5 unsuccessful searches
     */
    public GetStatistics() {
        this(100, 100, 1.5);
    }


    /**
     * Run your own 1000 case experiment using your own set number of names per test, and set your average unsucessful
     * searches.
     */
    public GetStatistics(int numberOfNamesAdd, int numberOfNamesSearch, double avgUnsuccessfulSearch) {
        int linearCapacity = calculateLinearCapacity(numberOfNamesAdd, avgUnsuccessfulSearch);
        int doubleCapacity = calculateDoubleCapacity(numberOfNamesAdd, avgUnsuccessfulSearch);
        linearHash = new LinearProbingWithCount<>(linearCapacity);
        doubleHash = new DoubleHashingWithCount<>(doubleCapacity);
        linearCollisions = new int[1000];
        doubleCollisions = new int[1000];
        addNames = new String[1000];
        searchNames = new String[10000];
        createDisjointNameArrays();
    }


    /**
     * Populates the thousandNames field with 1000 different names of length 2 and populates the tenThousandNames
     * field with 10,000 different names of length 3. Because both arrays contain names of different lengths, the names
     * between the two arrays are disjoint. The names in a single array are also disjoint because my nested loops
     * are going through every 2 and 3 character permutation of lowercase characters (and numbers up to 6) until the
     * limit of 1000 and 10000 are hit.
     *
     * lol is this O(1)? What is n in this experiment actually?
     */
    public void createDisjointNameArrays() {
        char[] charArray = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o',
                'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '1', '2', '3', '4',
                '5', '6'};
        int count = 0;
        for (int i = 0; i < 32; i++) {
            for (int j = 0; j < 32; j++) {
                for (int k = 0; k < 32 && count < 10000; k++) {
                    if (count < 1000) {
                        addNames[count] = "" + charArray[j] + charArray[k];
                    }
                    searchNames[count] = "" + charArray[i] + charArray[j] + charArray[k];
                    count++;
                }
            }
        }
    }

    public String[] getAddNames() {
        return addNames;
    }

    public String[] getSearchNames() {
        return searchNames;
    }


    public int calculateLinearCapacity(int namesPerTest, double avgUnsuccessfulSearch) {
        double loadFactor = 1.0 - Math.sqrt(1.0 / (2 * avgUnsuccessfulSearch - 1));
        return (int) (namesPerTest / loadFactor);
    }


    public int calculateDoubleCapacity(int namesPerTest, double avgUnsuccessfulSearch) {
        double loadFactor = 1.0 - (1.0 / avgUnsuccessfulSearch);
        return (int) (namesPerTest / loadFactor);
    }


    /**
     * @param namesToAdd number of names to add to the HashTable
     * @param namesToSearch number of names to Search per experiment
     */
    public void runExperiment(int namesToAdd, int namesToSearch) {
        Random r = new Random();
        int EXPERIMENT_COUNT = 1000;
        ArrayList<Integer> randomNumsAdd;
        ArrayList<Integer> randomNumsSearch;
        String tempName;

        for (int i = 0; i < EXPERIMENT_COUNT; i++) {
            linearHash.clear();
            doubleHash.clear();
//            randomNumsAdd = generateNUniqueIntegers(namesToAdd,1000);
//            for (int num : randomNumsAdd) {
//                tempName = addNames[num];
//                linearHash.add(tempName, tempName);
//                doubleHash.add(tempName, tempName);
//            }
            for (int j = 0; j < namesToAdd; j++){
                tempName = addNames[r.nextInt(1000)];
                linearHash.add(tempName, tempName);
                doubleHash.add(tempName, tempName);
            }
            linearHash.resetProbeCount();
            doubleHash.resetProbeCount();
//            randomNumsSearch = generateNUniqueIntegers(namesToSearch,10000);
//            for (int num: randomNumsSearch) {
//                tempName = searchNames[num];
//                linearHash.contains(tempName);
//                doubleHash.contains(tempName);
//            }
            for (int j = 0; j < namesToSearch; j++){
                tempName = searchNames[r.nextInt(10000)];
                linearHash.contains(tempName);
                doubleHash.contains(tempName);
            }
            linearCollisions[i] = linearHash.getProbeCount();
            doubleCollisions[i] = doubleHash.getProbeCount();
        }

        double linearCollisionAvg = computeAverageOfArray(linearCollisions);
        double doubleCollisionAvg = computeAverageOfArray(doubleCollisions);
        double linearCollisionSD = computeStandardDeviation(linearCollisionAvg, linearCollisions);
        double doubleCollisionSD = computeStandardDeviation(doubleCollisionAvg, doubleCollisions);

        System.out.println(Arrays.toString(linearCollisions));
        System.out.println();
        System.out.println(Arrays.toString(doubleCollisions));
        System.out.printf("The average number of probes for an unsuccessful search using linear collision resolution" +
                            " was: %.3f", linearCollisionAvg);
        System.out.printf(" (%.3f per search)", linearCollisionAvg/namesToSearch);
        System.out.println();
        System.out.printf("The average number of probes for an unsuccessful search using double hashing collision" +
                " resolution was: %.3f", doubleCollisionAvg);
        System.out.printf(" (%.3f per search)", doubleCollisionAvg/namesToSearch);
        System.out.println();
        System.out.printf("The standard deviation for the number of probes for an unsuccessful search using linear " +
                "collision resolution was: %.3f", linearCollisionSD);
        System.out.printf(" (%.3f per search)", linearCollisionSD/namesToSearch);
        System.out.println();
        System.out.printf("The standard deviation for the number of probes for an unsuccessful search using double " +
                "hashing collision resolution was: %.3f", doubleCollisionSD);
        System.out.printf(" (%.3f per search)", doubleCollisionSD/namesToSearch);

        System.out.println();
        System.out.println("Linear Hash Table Size: " + linearHash.getHashTableSize());
        System.out.println("Double Hashing Table Size: " + doubleHash.getHashTableSize());
        System.out.println("Linear Hash Load Factor: " + linearHash.getLoadFactor());
        System.out.println("Double Hashing Load Factor: " + doubleHash.getLoadFactor());
    }


    /**
     * Generates n unique random integers between 0 and given intervalEndpoint (exclusive)
     * @param n number of unique random integers you want in the set
     * @param intervalEndpoint the endpoint of the interval you want to search on (between 0 and this number, exclusive)
     * @return a set of n unique random integers between 0 and intervalEndpoint (exclusive)
     */
    public ArrayList<Integer> generateNUniqueIntegers(int n, int intervalEndpoint) {
        HashSet<Integer> randomNums = new HashSet<>();
        Random r = new Random();
        while (randomNums.size() < n) {
            randomNums.add(r.nextInt(intervalEndpoint));
        }

        ArrayList<Integer> randomNumsArray = new ArrayList<>();
        for (int nums : randomNums) {
            randomNumsArray.add(nums);
        }

        Collections.shuffle(randomNumsArray);

        return randomNumsArray;
    }


    public double computeAverageOfArray(int[] intArray) {
        int size = intArray.length;
        int sum = 0;
        for (int i = 0; i < size; i++) {
            sum += intArray[i];
        }
        return (double) sum / size;
    }


    public double computeStandardDeviation(double average, int[] intArray) {
        int size = intArray.length;
        double sum = 0.0;
        for (int i = 0; i < size; i++) {
            sum += Math.pow(intArray[i] - average, 2);
        }

        return Math.sqrt(sum/(size-1));
    }
}
