import java.util.Iterator;
import java.util.NoSuchElementException;

public class DoubleHashingWithCount<K, V> implements DictionaryInterface<K, V>
{
    // The dictionary:
    private int numberOfEntries;
    private static final int DEFAULT_CAPACITY = 11;        // Must be prime
    private static final int MAX_CAPACITY = 10000;

    // The hash table:
    private Entry<K, V>[] hashTable;
    private int tableSize;                                // Must be prime
    private static final int MAX_SIZE = 2 * MAX_CAPACITY; // Max size of hash table
    private boolean integrityOK = false;
    private static final double MAX_LOAD_FACTOR = 0.5;    // Fraction of hash table that can be filled
    private final Entry<K, V> AVAILABLE = new Entry<>(null, null); // Occupies locations in the hash table in the available state (locations whose entries were removed)

    // With Count:

    /** Number of probes total when searching with contains() */
    private int probeCount;

    public DoubleHashingWithCount()
    {
        this(DEFAULT_CAPACITY); // Call next constructor
    } // end default constructor


    public DoubleHashingWithCount(int initialCapacity)
    {
        initialCapacity = checkCapacity(initialCapacity);
        numberOfEntries = 0;    // Dictionary is empty
        probeCount = 0; // No searches have been done yet

        // Set up hash table:
        // Initial size of hash table is same as initialCapacity if it is prime;
        // otherwise increase it until it is prime size
        tableSize = getNextPrime(initialCapacity);
        checkSize(tableSize); // Check that the prime size is not too large

        // The cast is safe because the new array contains null entries
        @SuppressWarnings("unchecked")
        Entry<K, V>[] temp = (Entry<K, V>[])new Entry[tableSize];
        hashTable = temp;
        integrityOK = true;
    } // end constructor


    // -------------------------
// We've added this method to display the hash table for illustration and testing
// -------------------------
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

    public int getProbeCount() {
        return probeCount;
    }

    public void resetProbeCount() {
        probeCount = 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public V add(K key, V value)
    {
        checkIntegrity();
        if ((key == null) || (value == null))
            throw new IllegalArgumentException("Cannot add null to a dictionary.");
        else
        {
            V oldValue;                // Value to return

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
            } // end if

            // Ensure that hash table is large enough for another add
            if (isHashTableTooFull())
                enlargeHashTable();

            return oldValue;
        } // end if
    } // end add


//    /**
//     * Adds a new entry to this dictionary. If the given search key already exists in the dictionary, it replaces the
//     * value. The number of collisions it took to successfully add the entry to the dictionary is returned.
//     * @param key An object search key of the new entry.
//     * @param value An object associated with the search key.
//     * @return number of collisions before entry was added to the dictionary
//     */
//    public int addWithCount(K key, V value)
//    {
//        checkIntegrity();
//        if ((key == null) || (value == null))
//            throw new IllegalArgumentException("Cannot add null to a dictionary.");
//        else
//        {
//            int index = getHashIndex(key);
//
//            // Assertion: index is within legal range for hashTable
//            assert (index >= 0) && (index < hashTable.length);
//
//            if ( (hashTable[index] == null) || (hashTable[index] == AVAILABLE) )
//            { // Key not found, so insert new entry
//                hashTable[index] = new Entry<>(key, value);
//                numberOfEntries++;
//            }
//            else { // this shouldn't happen in our experiment there are no, I'll leave it for now to test for error
//                throw new IllegalArgumentException("Duplicate Key Found. No Keys should be Duplicate in this Experiment.");
//            }
//
//            // Ensure that hash table is large enough for another add
//            if (isHashTableTooFull())
//                enlargeHashTable();
//
//            return probeCount;
//        } // end if
//    } // end add


    /**
     * {@inheritDoc}
     */
    public V remove(K key)
    {
        checkIntegrity();
        V removedValue = null;

        int index = getHashIndex(key);

        if ((hashTable[index] != null) && (hashTable[index] != AVAILABLE))
        {
            // Key found; flag entry as removed and return its value
            removedValue = hashTable[index].getValue();
            hashTable[index] = AVAILABLE;
            numberOfEntries--;
        } // end if
        // Else not found; result is null

        return removedValue;
    } // end remove


    /**
     * {@inheritDoc}
     */
    public V getValue(K key)
    {
        checkIntegrity();
        V result = null;

        int index = getHashIndex(key);

        if ((hashTable[index] != null) && (hashTable[index] != AVAILABLE))
            result = hashTable[index].getValue(); // Key found; get value
        // Else not found; result is null

        return result;
    } // end getValue


    /**
     * {@inheritDoc}
     */
    public boolean contains(K key)
    {
        return getValue(key) != null;
    }

//    /**
//     * @param key An object search key of the desired entry.
//     * @return number of collisions from the search
//     */
//    public int containsWithCount(K key)
//    {
//        checkIntegrity();
//
//        int index = getHashIndex(key);
//
//        if ((hashTable[index] != null) && (hashTable[index] != AVAILABLE))
//            throw new IllegalArgumentException("We should never find key. Experiment failed."); // Key found;
//
//        return probeCount;
//    } // end contains


    public double getLoadFactor() {
        return (double) numberOfEntries/hashTable.length;
    }

    public int getHashTableSize() {
        return hashTable.length;
    }


    /**
     * {@inheritDoc}
     */
    public boolean isEmpty()
    {
        return numberOfEntries == 0;
    } // end isEmpty


    /**
     * {@inheritDoc}
     */
    public int getSize()
    {
        return numberOfEntries;
    } // end getSize


    /**
     * {@inheritDoc}
     */
    public final void clear()
    {
        checkIntegrity();
        for (int index = 0; index < hashTable.length; index++)
            hashTable[index] = null;

        numberOfEntries = 0;
    } // end clear


    /**
     * {@inheritDoc}
     */
    public Iterator<K> getKeyIterator()
    {
        return new KeyIterator();
    } // end getKeyIterator


    /**
     * {@inheritDoc}
     */
    public Iterator<V> getValueIterator()
    {
        return new ValueIterator();
    } // end getValueIterator



    private int getHashIndex(K key)
    {
        int hashIndex = key.hashCode() % hashTable.length;


        if (hashIndex < 0)
        {
            hashIndex = hashIndex + hashTable.length;
        } // end if

        probeCount++; // the initial probe

        // Check for and resolve collision.

        return getSecondHashIndex(hashIndex, key);
    } // end getHashIndex


    // Precondition: checkIntegrity has been called.
    private int getSecondHashIndex(int index, K key)
    {
        int PRIME = 7;
        int originalHashCode = index;
        int n = 0; // number of times we've used the double hash function, or seen an AVAILABLE entry.

        boolean found = false;
        int availableIndex = -1; // Index of first available location (from which an entry was removed)

        while ( !found && (hashTable[index] != null) ) {
            if (hashTable[index] != AVAILABLE) {
                if (key.equals(hashTable[index].getKey())) {
                    found = true; // Key found
                } else { // DOUBLE HASH FUNCTION
                    n++; // increment the number of times we've used the double hash function.
                    index = (originalHashCode + n * (PRIME - (originalHashCode % PRIME))) % hashTable.length;
                    probeCount++; // add to probe count every time we use the second hash function.
                }

            } else { // Skip entries that were removed (This should not happen in our experiment)!
                // Save index of first location in removed state
                if (availableIndex == -1) {
                    availableIndex = index;
                }

                // if we hit this code, then we have found another AVAILABLE entry, but we don't need to save the info,
                // just continue to search until we find null or find the key. Still consider this a probe.
                n++;
                index = (originalHashCode + n * (PRIME - (originalHashCode % PRIME))) % hashTable.length;
                probeCount++;
                System.out.println("Shouldn't have come here, double");
            } // end if
        } // end while
        // Assertion: Either key or null is found at hashTable[index]

        if (found || (availableIndex == -1) ) {
            return index;
        } else { // Index of either key or null
            return availableIndex;
        }// Index of an available location
    } // end getSecondIndexHash


    // Increases the size of the hash table to a prime >= twice its old size.
    // In doing so, this method must rehash the table entries.
    // Precondition: checkIntegrity has been called.
    private void enlargeHashTable()
    {
        Entry<K, V>[] oldTable = hashTable;
        int oldSize = hashTable.length;
        int newSize = getNextPrime(oldSize + oldSize);
        checkSize(newSize); // Check that the prime size is not too large

        // The cast is safe because the new array contains null entries
        @SuppressWarnings("unchecked")
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
        } // end for
    } // end enlargeHashTable

    // Returns true if lambda > MAX_LOAD_FACTOR for hash table;
    // otherwise returns false.
    private boolean isHashTableTooFull()
    {
        return numberOfEntries > MAX_LOAD_FACTOR * hashTable.length;
    } // end isHashTableTooFull

    // Returns a prime integer that is >= the given integer, but <= MAX_SIZE.
    private int getNextPrime(int anInteger)
    {
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

    // Returns true if the given integer is prime.
    private boolean isPrime(int anInteger)
    {
        boolean result;
        boolean done = false;

        // 1 and even numbers are not prime
        if ( (anInteger == 1) || (anInteger % 2 == 0) )
        {
            result = false;
        }

        // 2 and 3 are prime
        else if ( (anInteger == 2) || (anInteger == 3) )
        {
            result = true;
        }

        else // anInteger is odd and >= 5
        {
            assert (anInteger % 2 != 0) && (anInteger >= 5);

            // a prime is odd and not divisible by every odd integer up to its square root
            result = true; // assume prime
            for (int divisor = 3; !done && (divisor * divisor <= anInteger); divisor = divisor + 2)
            {
                if (anInteger % divisor == 0)
                {
                    result = false; // divisible; not prime
                    done = true;
                } // end if
            } // end for
        } // end if

        return result;
    } // end isPrime

    // Throws an exception if this object is not initialized.
    private void checkIntegrity()
    {
        if (!integrityOK)
            throw new SecurityException ("HashedDictionary object is corrupt.");
    } // end checkIntegrity

    // Ensures that the client requests a capacity
    // that is not too small or too large.
    private int checkCapacity(int capacity)
    {
        if (capacity < DEFAULT_CAPACITY)
            capacity = DEFAULT_CAPACITY;
        else if (capacity > MAX_CAPACITY)
            throw new IllegalStateException("Attempt to create a dictionary " +
                    "whose capacity is larger than " +
                    MAX_CAPACITY);
        return capacity;
    } // end checkCapacity

    // Throws an exception if the hash table becomes too large.
    private void checkSize(int size)
    {
        if (size > MAX_SIZE)
            throw new IllegalStateException("Dictionary has become too large.");
    } // end checkSize

    /**
     * Iterator object that iterates through the keys of this dictionary
     */
    private class KeyIterator implements Iterator<K>
    {
        private int currentIndex; // Current position in hash table
        private int numberLeft;   // Number of entries left in iteration

        private KeyIterator()
        {
            currentIndex = 0;
            numberLeft = numberOfEntries;
        } // end default constructor

        public boolean hasNext()
        {
            return numberLeft > 0;
        } // end hasNext

        public K next()
        {
            K result = null;

            if (hasNext())
            {
                // Skip table locations that do not contain a current entry
                while ( (hashTable[currentIndex] == null) || hashTable[currentIndex] == AVAILABLE )
                {
                    currentIndex++;
                } // end while

                result = hashTable[currentIndex].getKey();
                numberLeft--;
                currentIndex++;
            }
            else
                throw new NoSuchElementException();

            return result;
        } // end next

        public void remove()
        {
            throw new UnsupportedOperationException();
        } // end remove
    } // end KeyIterator

    /**
     * Iterator object that iterates through the values of this dictionary.
     */
    private class ValueIterator implements Iterator<V>
    {
        private int currentIndex;
        private int numberLeft;

        private ValueIterator()
        {
            currentIndex = 0;
            numberLeft = numberOfEntries;
        } // end default constructor

        public boolean hasNext()
        {
            return numberLeft > 0;
        } // end hasNext

        public V next()
        {
            V result = null;

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
        } // end next

        public void remove()
        {
            throw new UnsupportedOperationException();
        } // end remove
    } // end ValueIterator


    /**
     * Object that holds the key, value pairs for this dictionary
     * @param <K> generic of type K, for key
     * @param <V> generic of type V, for value
     */
    protected static final class Entry<K, V>
    {
        private K key;
        private V value;

        private Entry(K searchKey, V dataValue)
        {
            key = searchKey;
            value = dataValue;
        } // end constructor

        private K getKey()
        {
            return key;
        } // end getKey

        private V getValue()
        {
            return value;
        } // end getValue

        private void setValue(V newValue)
        {
            value = newValue;
        } // end setValue
    } // end Entry


    /**
     * Object that holds the hashCode and collision count when an entry is added using addWithCount()
     */
    private static class HashCapsule {
        private int hashCode;
        private int collisionCount;

        HashCapsule(int hashCode, int collisionCount) {
            this.hashCode = hashCode;
            this.collisionCount = collisionCount;
        }

        public int getHashCode() {
            return hashCode;
        }

        public int getCollisionCount() {
            return collisionCount;
        }
    }
} // end HashedDictionary


