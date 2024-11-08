import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Hashed Dictionary that resolves collisions with linear probing. Contains extra fields and methods for counting
 * the amount of probes done over chosen intervals of time. Because this dictionary can be used for experiments it
 * does not require initial hashTable size to be prime, but will rehash to prime sizes.
 *
 * Time Complexity: I don't talk a lot about Time Complexity for specific methods in  this project, so I'll do it
 * generally for HashTables. The benefits of using a HashTable is that it has amortized O(1) time complexity for add,
 * delete, and, search. Amortized means, essentially, there may be rare occasions where time complexity is not O(1),
 * such as when we add and need to enlarge the table and do a rehash, or when there are collisions, having to do
 * multiple probes to find a location, up to O(n). The idea is that this happens infrequently enough that essentially
 * these operations run in O(1) time.
 *
 * This is so great, why don't we use a HashTable all the time then? The largest downside to HashTable is that
 * they are not sorted, so they cannot be used for contexts where that is needed. Additionally, they require more memory
 * than some other data structures as, you need arrays much larger than the data you are storing to keep your
 * load factor low so collisions are infrequent, so you can keep that desired O(1) time complexity.
 *
 * @param <K> generic of type K for the search key
 * @param <V> generic of type V for the value
 */
public class LinearProbingWithCount<K, V> implements DictionaryInterface<K, V>
{
    // The dictionary:

    /** Number of elements in the dictionary. */
    private int numberOfEntries;
    /** Default capacity of the dictionary when using empty constructor*/
    private static final int DEFAULT_CAPACITY = 11;
    /** Max capacity of the dictionary. */
    private static final int MAX_CAPACITY = 10000;

    // The hash table:

    /** Table where dictionary elements are stored. */
    private Entry<K, V>[] hashTable;
    /** Number of cells in the entire hashTable */
    private int tableSize;
    /** Maximum size of the hashTable */
    private static final int MAX_SIZE = 2 * MAX_CAPACITY;
    /** Checks that nothing went wrong during hashTable initialization */
    private boolean integrityOK = false;
    /** Fraction of the hash table that can be filled. */
    private static final double MAX_LOAD_FACTOR = 0.5;
    /** Occupies locations in the hash table in the available state (locations whose entries were removed) */
    private final Entry<K, V> AVAILABLE = new Entry<>(null, null);

    //With Probe:

    /** Number of probes total when using any function that calls getHashIndex() or linearProbe() until the counter
     * is reset using resetLinearProbe() */
    private int probeCount;


    /**
     * Default Constructor
     */
    public LinearProbingWithCount()
    {
        this(DEFAULT_CAPACITY); // Call full constructor
    }


    /**
     * Full Constructor
     * @param initialCapacity Initial capacity you want to set your hashTable at, (will change to the next highest
     *                        prime number, if not already prime).
     */
    public LinearProbingWithCount(int initialCapacity)
    {
        initialCapacity = checkCapacity(initialCapacity);
        numberOfEntries = 0;    // Dictionary is empty
        probeCount = 0;  // No searches have been done yet

        // Set up hash table:
        tableSize = initialCapacity;
        checkSize(tableSize); // Check that the size is not too large

        // The cast is safe because the new array contains null entries
        @SuppressWarnings({"unchecked", "rawtypes"})
        Entry<K, V>[] temp = (Entry<K, V>[])new Entry[tableSize];
        hashTable = temp;
        integrityOK = true;
    }


    // -------------------------
    // We've added this method to display the hash table for illustration and testing
    // -------------------------

    /**
     * Displays the hashTable.
     */
    public void displayHashTable()
    {
        checkIntegrity();
        for (int index = 0; index < hashTable.length; index++)
        {
            if (hashTable[index] == null)
                System.out.println("null ");
            else if (hashTable[index] == AVAILABLE)
                System.out.println("available - removed state");
            else
                System.out.println(hashTable[index].getKey() + " " + hashTable[index].getValue());
        } // end for
        System.out.println();
    } // end displayHashTable
    // -------------------------


    /**
     * Retrieves the current probeCount
     *
     * @return the current probeCount
     */
    public int getProbeCount() {
        return probeCount;
    }


    /**
     * resets the probeCount to 0.
     */
    public void resetProbeCount() {
        probeCount = 0;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public V add(K key, V value) {
        checkIntegrity();
        if ((key == null) || (value == null))
            throw new IllegalArgumentException("Cannot add null to a dictionary.");
        else
        {
            V oldValue; // Value to return

            int index = getHashIndex(key);

            // Assertion: index is within legal range for hashTable
            assert (index >= 0) && (index < hashTable.length);

            if ( (hashTable[index] == null) || (hashTable[index] == AVAILABLE) )
            { // Key not found, so insert new entry
                hashTable[index] = new Entry<>(key, value);
                numberOfEntries++;
                oldValue = null;
            }
            else
            { // Key found; get old value for return and then replace it
                oldValue = hashTable[index].getValue();
                hashTable[index].setValue(value);
            }

            // Ensure that hash table is large enough for another add
            if (isHashTableTooFull())
               enlargeHashTable();

            return oldValue;
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public V remove(K key) {
        checkIntegrity();
        V removedValue = null;

        int index = getHashIndex(key);

        if ((hashTable[index] != null) && (hashTable[index] != AVAILABLE)) {
            // Key found; flag entry as removed and return its value
            removedValue = hashTable[index].getValue();
            hashTable[index] = AVAILABLE;
            numberOfEntries--;
        }
        // Else not found; result is null
        return removedValue;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public V getValue(K key) {
        checkIntegrity();
        V result = null;

        int index = getHashIndex(key);

        if ((hashTable[index] != null) && (hashTable[index] != AVAILABLE))
            result = hashTable[index].getValue(); // Key found; get value
        // Else not found; result is null

        return result;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean contains(K key) {
        return getValue(key) != null;
    }


    /** Probably delete this
     * @return load factor
     */
    public double getLoadFactor() {
        return (double) numberOfEntries/hashTable.length;
    }


    /** Probably delete this
     * @return hash table length
     */
    public int getHashTableSize() {
        return hashTable.length;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEmpty() {
        return numberOfEntries == 0;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public int getSize() {
        return numberOfEntries;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public final void clear() {
        checkIntegrity();
        for (int index = 0; index < hashTable.length; index++)
            hashTable[index] = null;

        numberOfEntries = 0;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Iterator<K> getKeyIterator() {
        return new KeyIterator();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Iterator<V> getValueIterator() {
        return new ValueIterator();
    }


    /**
     * Finds and retrieves an unused or available hashIndex for this key.
     * @param key An object search key we want hashIndex for
     * @return first available or unused hashIndex for this search key
     */
    private int getHashIndex(K key) {
        int hashIndex = key.hashCode() % hashTable.length;

        if (hashIndex < 0) {
            hashIndex = hashIndex + hashTable.length;
        }

        probeCount++; // count initial probe

        // Check for and resolve collision
        return linearProbe(hashIndex, key);
    }


    /**
     * Check to see if the initial hashIndex is unused or available, and if it is not, finds one via linear probing.
     * @param index the initial hashIndex for this key
     * @param key An object search key we want hashIndex for
     * @return the initial hashIndex if it is unused or available, or the first available or unused one.
     * Time Complexity: O(n), possibly needs to check every index in the hash table.
     */
    private int linearProbe(int index, K key) {
        boolean found = false;
        int availableIndex = -1; // Index of first available location (from which an entry was removed)

        while ( !found && (hashTable[index] != null) )
        {
            if (hashTable[index] != AVAILABLE)
            {
                if (key.equals(hashTable[index].getKey())) {
                    found = true; // Key found
                } else { // Follow probe sequence
                    index = (index + 1) % hashTable.length; // Linear probing
                    probeCount++; // Add to probe count for every linear probe we do.
                }
            }
            else { // Skip entries that were removed.
                // Save index of first location in removed state
                if (availableIndex == -1) {
                    availableIndex = index;
                }

                // if we hit this code, then we have found another AVAILABLE entry, but we don't need to save the info,
                // just continue to search until we find null or find the key. Still consider this a probe.
                index = (index + 1) % hashTable.length; // Linear probing
                probeCount++;
            }
        }

        // Assertion: Either key or null is found at hashTable[index]
        if (found || (availableIndex == -1) ) { // Index of either key or null
            return index;
        } else { // Index of an available location
            return availableIndex;
        }
    } // end linearProbe


    /**
     * Increases the size of a hash table to a prime greater than or equal to twice its old size.
     * Then, rehashes the entries.
     */
    private void enlargeHashTable() {
        Entry<K, V>[] oldTable = hashTable;
        int oldSize = hashTable.length;
        int newSize = getNextPrime(oldSize + oldSize);
        checkSize(newSize); // Check that the prime size is not too large
        tableSize = newSize;

        // The cast is safe because the new array contains null entries
        @SuppressWarnings({"unchecked", "rawtypes"})
        Entry<K, V>[] tempTable = (Entry<K, V>[])new Entry[newSize]; // Increase size of array
        hashTable = tempTable;
        numberOfEntries = 0; // Reset number of dictionary entries, since
        // it will be incremented by add during rehash

        // Rehash dictionary entries from old array to the new and bigger array;
        // skip both null locations and removed entries
        for (int index = 0; index < oldSize; index++)
        {
            if ( (oldTable[index] != null) && (oldTable[index] != AVAILABLE) )
                add(oldTable[index].getKey(), oldTable[index].getValue());
        }
    }


    /**
     * Checks if the current load factor (lambda) is greater than MAX_LOAD_FACTOR
     * @return true if lambda is greater than MAX_LOAD_FACTOR for hash table; otherwise returns false.
     */
    private boolean isHashTableTooFull() {
        return numberOfEntries > MAX_LOAD_FACTOR * hashTable.length;
    }


    /**
     * Returns a prime integer that is greater than or equal to the given integer, but less than or equal to MAX_SIZE.
     * @param anInteger any positive integer
     * @return a prime integer
     */
    private int getNextPrime(int anInteger) {
        // if even, add 1 to make odd
        if (anInteger % 2 == 0)
        {
            anInteger++;
        } // end if

        // test odd integers
        while (!isPrime(anInteger))
        {
            anInteger = anInteger + 2;
        } // end while

        return anInteger;
    } // end getNextPrime


    /**
     * Determines whether an integer is prime.
     * @param anInteger any integer
     * @return true if the given integer is prime, false otherwise.
     */
    private boolean isPrime(int anInteger) {
        boolean result;

        // 2 and 3 are prime
        if ( (anInteger == 2) || (anInteger == 3) ) {
            result = false;
        }

        // 1 and even numbers are not prime (I changed this from the original program).
        else if ( (anInteger == 1) || (anInteger % 2 == 0) )
        {
            result = true;
        }

        else // anInteger is odd and >= 5
        {
            assert (anInteger % 2 != 0) && (anInteger >= 5);

            // a prime is odd and not divisible by every odd integer up to its square root
            result = true; // assume prime
            for (int divisor = 3; divisor * divisor <= anInteger; divisor = divisor + 2)
            {
                if (anInteger % divisor == 0) {
                    result = false; // divisible; not prime
                    break;
                }
            }
        }

        return result;
    }


    /**
     * Throws an exception if this object is not initialized.
     * @throws SecurityException if object is not initialized
     */
    private void checkIntegrity() {
        if (!integrityOK)
            throw new SecurityException ("HashedDictionary object is corrupt.");
    }


    /**
     * Ensures that the client requests a capacity that is not too small or too large.
     * @param capacity integer capacity to check
     * @return capacity if it's less than MAX_CAPACITY
     * @throws IllegalStateException if there is an attempt to create a dictionary larger than MAX_CAPACITY
     */
    private int checkCapacity(int capacity) {
        if (capacity < DEFAULT_CAPACITY)
            capacity = DEFAULT_CAPACITY;
        else if (capacity > MAX_CAPACITY)
            throw new IllegalStateException("Attempt to create a dictionary " +
                    "whose capacity is larger than " +
                    MAX_CAPACITY);
        return capacity;
    }


    /**
     * Verifies size of the hashTable itself.
     * @param size current size of hashTable
     * @throws IllegalStateException if hashTable exceeds MAX_SIZE
     */
    private void checkSize(int size)
    {
        if (size > MAX_SIZE)
            throw new IllegalStateException("Dictionary has become too large.");
    }


    /**
     * Iterator object that iterates through the keys of this dictionary
     */
    private class KeyIterator implements Iterator<K>
    {
        /** Current position in hash table */
        private int currentIndex;
        /** Number of entries left in iteration */
        private int numberLeft;


        /**
         * Default Constructor for KeyIterator
         */
        private KeyIterator() {
            currentIndex = 0;
            numberLeft = numberOfEntries;
        }


        /**
         * Checks if there is another element in the iteration.
         * @return True if there is another element in iteration, false otherwise.
         */
        public boolean hasNext() {
            return numberLeft > 0;
        }


        /**
         * Returns the next element in the iteration.
         *
         * @return The next element in the iteration.
         * @throws NoSuchElementException If there is no next element in the iteration.
         */
        public K next() {
            K result;

            if (hasNext()) {
                // Skip table locations that do not contain a current entry
                while ( (hashTable[currentIndex] == null) || hashTable[currentIndex] == AVAILABLE ) {
                    currentIndex++;
                } // end while

                result = hashTable[currentIndex].getKey();
                numberLeft--;
                currentIndex++;
            } else {
                throw new NoSuchElementException();
            }

            return result;
        }


        /**
         * Remove method not supported for this iterator.
         * @throws UnsupportedOperationException this iterator does not allow you to remove elements
         */
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }


    /**
     * Iterator object that iterates through the values of this dictionary.
     */
    private class ValueIterator implements Iterator<V>
    {
        /** Current position in hash table */
        private int currentIndex;
        /** Number of entries left in iteration */
        private int numberLeft;

        /**
         * Default Constructor for ValueIterator
         */
        private ValueIterator()
        {
            currentIndex = 0;
            numberLeft = numberOfEntries;
        }


        /**
         * Checks if there is another element in the iteration.
         * @return True if there is another element in iteration, false otherwise.
         */
        public boolean hasNext() {
            return numberLeft > 0;
        }


        /**
         * Returns the next element in the iteration.
         *
         * @return The next element in the iteration.
         * @throws NoSuchElementException If there is no next element in the iteration.
         */
        public V next()
        {
            V result;

            if (hasNext())
            {
                // Skip table locations that do not contain a current entry
                while ( (hashTable[currentIndex] == null) || hashTable[currentIndex] == AVAILABLE )
                {
                    currentIndex++;
                } // end while

                result = hashTable[currentIndex].getValue();
                numberLeft--;
                currentIndex++;
            }
            else
                throw new NoSuchElementException();

            return result;
        }


        /**
         * Remove method not supported for this iterator.
         * @throws UnsupportedOperationException this iterator does not allow you to remove elements
         */
        public void remove()
        {
            throw new UnsupportedOperationException();
        }
    } // end ValueIterator


    /**
     * Object that holds the key, value pairs for this dictionary
     * @param <K> generic of type K, for key
     * @param <V> generic of type V, for value
     */
    protected static final class Entry<K, V>
    {
        /** Object search key for this dictionary */
        private final K key;
        /** Value for this dictionary */
        private V value;


        /**
         * Full constructor for Entry
         * @param searchKey object search key we want to store
         * @param dataValue value we want to store with this search key
         */
        private Entry(K searchKey, V dataValue) {
            key = searchKey;
            value = dataValue;
        }


        /**
         * Returns the search key for this Entry.
         * @return search key for this Entry
         */
        private K getKey() {
            return key;
        }


        /**
         * Returns the Value for this Entry.
         * @return Value for this Entry
         */
        private V getValue() {
            return value;
        } // end getValue


        /**
         * sets a new value for this Entry
         * @param newValue new value to set for this Entry
         */
        private void setValue(V newValue) {
            value = newValue;
        }
    } // end Entry
} // end HashedDictionary