import java.util.Iterator;
import java.util.NoSuchElementException;
//TODO: NO CHANGES HAVE BEEN MADE YET, GO THROUGH THIS CLASS AND TAILOR TO YOUR NEEDS FOR THIS PROJECT
public class LinearProbingWithCount<K, V> implements DictionaryInterface<K, V>
{
    // The dictionary:
    private int numberOfEntries;
    private static final int DEFAULT_CAPACITY = 5;        // Must be prime
    private static final int MAX_CAPACITY = 10000;

    // The hash table:
    private Entry<K, V>[] hashTable;
    private int tableSize;                                // Must be prime
    private static final int MAX_SIZE = 2 * MAX_CAPACITY; // Max size of hash table
    private boolean integrityOK = false;
    private static final double MAX_LOAD_FACTOR = 0.5;    // Fraction of hash table that can be filled
    // Occupies locations in the hash table in the available state (locations whose entries were removed)
    private final Entry<K, V> AVAILABLE = new Entry<>(null, null);

    public LinearProbingWithCount()
    {
        this(DEFAULT_CAPACITY); // Call next constructor
    } // end default constructor

    public LinearProbingWithCount(int initialCapacity)
    {
        initialCapacity = checkCapacity(initialCapacity);
        numberOfEntries = 0;    // Dictionary is empty

        // Set up hash table:
        // Initial size of hash table is same as initialCapacity if it is prime;
        // otherwise increase it until it is prime size
        int tableSize = getNextPrime(initialCapacity);
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

    public V add(K key, V value)
    {
        checkIntegrity();
        if ((key == null) || (value == null))
            throw new IllegalArgumentException("Cannot add null to a dictionary.");
        else
        {
            V oldValue; // Value to return

            HashCapsule tempCapsule = getHashIndex(key);
            int index = tempCapsule.getHashCode();

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
    }// end add

    public int addWithCount(K key, V value)
    {
        checkIntegrity();
        if ((key == null) || (value == null))
            throw new IllegalArgumentException("Cannot add null to a dictionary.");
        else
        {
            HashCapsule tempCapsule = getHashIndex(key);
            int index = tempCapsule.getHashCode();

            // Assertion: index is within legal range for hashTable
            assert (index >= 0) && (index < hashTable.length);

            if ( (hashTable[index] == null) || (hashTable[index] == AVAILABLE) )
            { // Key not found, so insert new entry
                hashTable[index] = new Entry<>(key, value);
                numberOfEntries++;
            }
            else { // this shouldn't happen in our experiment there are no, I'll leave it for now to test for error
                throw new IllegalArgumentException("Duplicate Key Found. No Keys should be Duplicate in this Experiment.");
            }

            // Ensure that hash table is large enough for another add
            if (isHashTableTooFull())
                enlargeHashTable();

            return tempCapsule.getCollisionCount();
        } // end if
    } // end add

    public V remove(K key)
    {
        checkIntegrity();
        V removedValue = null;

        HashCapsule tempCapsule = getHashIndex(key);
        int index = tempCapsule.getHashCode();

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

    public V getValue(K key)
    {
        checkIntegrity();
        V result = null;

        HashCapsule tempCapsule = getHashIndex(key);
        int index = tempCapsule.getHashCode();

        if ((hashTable[index] != null) && (hashTable[index] != AVAILABLE))
            result = hashTable[index].getValue(); // Key found; get value
        // Else not found; result is null

        return result;
    } // end getValue

    public boolean contains(K key)
    {
        return getValue(key) != null;
    } // end contains

    public boolean isEmpty()
    {
        return numberOfEntries == 0;
    } // end isEmpty

    public int getSize()
    {
        return numberOfEntries;
    } // end getSize

    public final void clear()
    {
        checkIntegrity();
        for (int index = 0; index < hashTable.length; index++)
            hashTable[index] = null;

        numberOfEntries = 0;
    } // end clear

    public Iterator<K> getKeyIterator()
    {
        return new KeyIterator();
    } // end getKeyIterator

    public Iterator<V> getValueIterator()
    {
        return new ValueIterator();
    } // end getValueIterator

    private HashCapsule getHashIndex(K key)
    {
        int hashIndex = key.hashCode() % hashTable.length;

        if (hashIndex < 0)
        {
            hashIndex = hashIndex + hashTable.length;
        } // end if

        int tempHashIndex = hashIndex;
        // Check for and resolve collision
        hashIndex = linearProbe(hashIndex, key);
        // hashIndex = quadraticProbe(hashIndex, key);

        //calculate collisions
        int collisionCount = tempHashIndex - hashIndex;
        if (collisionCount < 0) {
            collisionCount = (tableSize - tempHashIndex) + hashIndex;
        }
        HashCapsule tempCapsule = new HashCapsule(hashIndex, collisionCount);

        return tempCapsule;
    } // end getHashIndex


    // Precondition: checkIntegrity has been called.
    private int linearProbe(int index, K key)
    {
        boolean found = false;
        int availableIndex = -1; // Index of first available location (from which an entry was removed)

        while ( !found && (hashTable[index] != null) )
        {
            if (hashTable[index] != AVAILABLE)
            {
                if (key.equals(hashTable[index].getKey()))
                    found = true; // Key found
                else             // Follow probe sequence
                    index = (index + 1) % hashTable.length;         // Linear probing
            }
            else // Skip entries that were removed
            {
                // Save index of first location in removed state
                if (availableIndex == -1)
                    availableIndex = index;

                index = (index + 1) % hashTable.length;            // Linear probing
            } // end if
        } // end while
        // Assertion: Either key or null is found at hashTable[index]

        if (found || (availableIndex == -1) )
            return index;                                      // Index of either key or null
        else
            return availableIndex;                          // Index of an available location
    } // end linearProbe



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

    protected final class Entry<K, V>
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


