import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Random;

/**
 * Object that allows you to run experiments on unsuccessful searches of HashedDictionaries that resolve collisions
 * using open addressing methods of Linear Probing and Double Hashing. Each experiment contains 1000 tests of n names
 * (where you can choose the n names). The experiment run will always result in unsuccessful searches. You can set up
 * your experiment to have average number of unsuccessful searches of your choosing, or use the default constructor for
 * a test of 100 names added to the table for an experiment where there are on average 1.5 unsuccessful searches.
 */
public class GetStatistics {
    /** Hashed Dictionary that resolves collisions with Linear Probing */
    private final LinearProbingWithCount<String, String> linearHash;
    /** Hashed Dictionary that resolves collisions with Double Hashing */
    private final DoubleHashingWithCount<String, String> doubleHash;
    /** Array that holds the 1000 unique strings, must be disjoint from searchNames */
    private final String[] addNames;
    /** Array that holds 10000 unique strings, must be disjoint from addNames */
    private final String[] searchNames;
    /** Array that holds the total number of probes for each of 1000 total tests using the linear probing table */
    private int[] linearProbes;
    /** Array that holds the total number of probes for each of 1000 total tests using the double hashing table */
    private int[] doubleHashProbes;


    /**
     * Default Constructor
     * Sets up basic experiment of 100 names for 1.5 unsuccessful searches.
     */
    public GetStatistics() {
        this(100,1.5);
    }


    /**
     * Full Constructor.
     *  * Run your own 1000 case experiment using your own set number of names per test, and set your own average
     *  * unsuccessful searches. Size of hash table will be calculated based on both parameters.
     *
     * @param numberOfNamesAdd number of unique names you want to add to each hash table
     * @param avgUnsuccessfulSearch average number of unsuccessful searches you want during the experiment.
     */
    public GetStatistics(int numberOfNamesAdd, double avgUnsuccessfulSearch) {
        int linearCapacity = calculateLinearCapacity(numberOfNamesAdd, avgUnsuccessfulSearch);
        int doubleCapacity = calculateDoubleCapacity(numberOfNamesAdd, avgUnsuccessfulSearch);
        linearHash = new LinearProbingWithCount<>(linearCapacity);
        doubleHash = new DoubleHashingWithCount<>(doubleCapacity);
        linearProbes = new int[1000];
        doubleHashProbes = new int[1000];
        addNames = new String[1000];
        searchNames = new String[10000];
        createDisjointNameArrays();
    }


    /**
     * Populates the addNames field with 1000 different names of length 2 and populates the searchNames
     * field with 10,000 different names of length 3. Because both arrays contain names of different lengths, the names
     * between the two arrays are disjoint. The names in a single array are also disjoint because my nested loops
     * are going through every 2 and 3 character permutation of lowercase characters (and numbers up to 6) until the
     * limit of 1000 and 10000 are hit. Both of these disjoint attributes are tested for in my unit tests.
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


    /**
     * Returns the array of names that are added to the hashTable. (Public for testing purposes).
     * @return the array of names that we can randomly add to the hashTable
     */
    public String[] getAddNames() {
        return addNames;
    }

    /**
     * Returns the array of names that are usd to search to the hashTable. (Public for testing purposes).
     * @return the array of names that we can randomly choose to search the hashTable
     */
    public String[] getSearchNames() {
        return searchNames;
    }


    /** For linear probing, the formula for the number of unsuccessful searches is 0.5{ 1+ 1 / [Math.pow(1-lambda), 2] }
     * where lambda is the load factor of the hashTable that average occurs at. This method caculates that load factor
     * using this formula and given parameters. It then uses the load factor formula:
     * lambda = (number of entries in dictionary) / (number of entries in hash table),
     * to calculate the size that the hashTable needs to be for the given parameters.
     *
     * In the case ouf our experiment, for the linear hash table, lambda = (2 - sqrt(2))/ 2, which is approximately
     * 0.2929, which would lead to a hash table of size 341. Since 341 is not prime, our dictionary bumps this size up
     * to the next largest prime, which is 347.
     * @param namesPerTest number of names you want to add to the hashTable for the test
     * @param avgUnsuccessfulSearch number of average unsuccessful searches you want
     * @return the hashTable capacity that matches the parameters given
     */
    public int calculateLinearCapacity(int namesPerTest, double avgUnsuccessfulSearch) {
        double loadFactor = 1.0 - Math.sqrt(1.0 / (2 * avgUnsuccessfulSearch - 1));
        return (int) Math.ceil((namesPerTest / loadFactor));
    }

    /** For double hashing, the formula for the number of unsuccessful searches is 1 + [1 / (1-lambda)], where lambda
     * is the load factor of the hashTable that average occurs at. This method calculates that load factor using this
     * formula and given parameters. It then uses the load factor formula:
     * lambda = (number of entries in dictionary) / (number of entries in hash table),
     * to calculate the size that the hashTable needs to be for the given parameters.
     *
     * In the case ouf our experiment, for the linear hash table, lambda = 1/3, which would lead to a hash table of
     * size 300. Since 300 is not prime, our dictionary bumps this size up to the next largest prime, which is 307.
     * @param namesPerTest number of names you want to add to the hashTable for the test
     * @param avgUnsuccessfulSearch number of average unsuccessful searches you want
     * @return the hashTable capacity that matches the parameters given
     */
    public int calculateDoubleCapacity(int namesPerTest, double avgUnsuccessfulSearch) {
        double loadFactor = 1.0 - (1.0 / avgUnsuccessfulSearch);
        return (int) Math.ceil((namesPerTest / loadFactor));
    }


    /**
     * Runs a 1000 experiments where parameters are used to set the number of names added to the hash tables
     * and the number of names to search on each experiment. Prints data about the average number of probes and the
     * standard deviation for both hash table.
     * @param namesToAdd number of names to add to both hash table
     * @param namesToSearch number of names to search per experiment
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
            randomNumsAdd = generateNUniqueIntegers(namesToAdd,1000);
            for (int num : randomNumsAdd) {
                tempName = addNames[num];
                linearHash.add(tempName, tempName);
                doubleHash.add(tempName, tempName);
            }
            linearHash.resetProbeCount();
            doubleHash.resetProbeCount();
            randomNumsSearch = generateNUniqueIntegers(namesToSearch,10000);
            for (int num: randomNumsSearch) {
                tempName = searchNames[num];
                linearHash.contains(tempName);
                doubleHash.contains(tempName);
            }
            linearProbes[i] = linearHash.getProbeCount();
            doubleHashProbes[i] = doubleHash.getProbeCount();
        }

        double linearCollisionAvg = computeAverageOfArray(linearProbes);
        double doubleCollisionAvg = computeAverageOfArray(doubleHashProbes);
        double linearCollisionSD = computeStandardDeviation(linearProbes);
        double doubleCollisionSD = computeStandardDeviation(doubleHashProbes);

        System.out.println(Arrays.toString(linearProbes));
        System.out.println();
        System.out.println(Arrays.toString(doubleHashProbes));
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
     * Generates n unique random integers between 0 and given intervalEndpoint (exclusive).
     * @param n number of unique random integers you want in the set
     * @param intervalEndpoint the endpoint of the interval you want to search on (between 0 and this number, exclusive)
     * @return a set of n unique random integers, in a random order, between 0 and intervalEndpoint (exclusive)
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


    /**
     * Computes and returns the average of an array of integers.
     *
     * @param intArray an array of integers
     * @return the average of the array
     */
    public double computeAverageOfArray(int[] intArray) {
        int size = intArray.length;
        int sum = 0;
        for (int i = 0; i < size; i++) {
            sum += intArray[i];
        }
        return (double) sum / size;
    }


    /**
     * Computes and returns the standard deviation of an array of integers.
     * @param intArray an array of integers
     * @return the standard deviation of the array.
     */
    public double computeStandardDeviation(int[] intArray) {
        int size = intArray.length;
        double average = computeAverageOfArray(intArray);
        double sum = 0.0;
        for (int i = 0; i < size; i++) {
            sum += Math.pow(intArray[i] - average, 2);
        }

        return Math.sqrt(sum/(size-1));
    }
}
