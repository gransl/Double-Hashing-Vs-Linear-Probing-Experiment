import java.util.Scanner;

public class Main {
    /*
     * For linear probing, the formula for the number of unsuccessful searches is 0.5{ 1+ 1 / [Math.pow(1-lambda), 2] }
     * where lambda is the load factor of the hashTable that average occurs at. This method calculates that load factor
     * using this formula and given parameters. It then uses the load factor formula:
     * lambda = (number of entries in dictionary) / (number of entries in hash table),
     * to calculate the size that the hashTable needs to be for the given parameters.
     *
     * In the case of our experiment, for the linear hash table, lambda = (2 - sqrt(2))/ 2, which is approximately
     * 0.2929, which would lead to a hash table of around size 342.
     *
     * For double hashing, the formula for the number of unsuccessful searches is 1 + [1 / (1-lambda)], where lambda
     * is the load factor of the hashTable that average occurs at. This method calculates that load factor using this
     * formula and given parameters. It then uses the load factor formula:
     * lambda = (number of entries in dictionary) / (number of entries in hash table),
     * to calculate the size that the hashTable needs to be for the given parameters.
     *
     * In the case of our experiment, for the double hash table, lambda = 1/3, which would lead to a hash table of
     * size 300.
     *
     * Between the two experiments, the average number of probes for an unsuccessful search remained the same on a
     * per-search basis. For the linear probing and double hashing I am seeing on average 1.5 probes per search.
     * This is to be expected, adding more of the same kinds of values will have little effect on the mean,
     *  as we are dividing by the number of values we are adding in.
     *
     * The standard deviations per search for the 1000 name search were less than the 100 name experiment. I
     * was getting about 0.15 SD per search for both linear probing and 0.09 double hashing in the 100 name search experiment,
     * but this dropped to about 0.09 and 0.3 for linear probing and double hashing respectively for the 1000 name experiment.
     * This is to be expected, as increasing the sample size often can show a decrease in spread. The more experiments we
     * have, the less likely our standard deviation will be affected by outliers, decreasing our spread.
     *
     * We can see both of these facts hold true as we increase our search to 10,000 names. The average number of probes
     * unsuccessful search is holding at 1.5 for both hash tables, but the standard deviation has again dropped
     * per search for both tables as well.
     */
    public static void main(String[] args) {
        Scanner console = new Scanner(System.in);
        System.out.println("*** Linear Probing vs. Double Hashing Experiment Program *** ");
        menu(console);
    }

    public static void menu(Scanner console) {
        //variables
        int mode = -1;

        //menu
        do {
            System.out.println();
            System.out.println("Choose from the following options: ");
            System.out.println("1. Run Project Experiment with Analysis (Prof. Lloyd pick this one)");
            System.out.println("2. Run Project Experiment with different name set.");
            System.out.println("3. Run Experiment with Double Hashing Table with different primes.");
            System.out.println("4. Program Description.");
            System.out.println("0. Quit");
            System.out.print("Choose an option 0-4: ");

            while (!console.hasNextInt()) {
                console.next();
                System.out.println();
                System.out.println("That is not a valid option.");
            }

            mode = console.nextInt();
            System.out.println();

            switch (mode) {
                case 1:
                    basicExperiment();
                    break;
                case 2:
                    basicExperimentPlus();
                    break;
                case 3:
                    runDoubleHashingExperiment();
                    break;
                case 4:
                    printDescription();
                    break;
                default:
                    System.out.print("Please enter 0-4: ");
                    break;
            }
        } while (mode != 0);
    }

    public static void basicExperiment(){
        GetStatistics newStat = new GetStatistics(false);
        System.out.println("********* Base Experiment + 10,000 Name Search *********");
        System.out.println("WARNING: Attempting to use a Double Hashing Table with a Composite Number. May Need to Be Rerun.");
        try {
            newStat.runExperiment(100, 100, true);
            newStat.runExperiment(100, 1000, true);
            newStat.runExperiment(100, 10000, false);
            printExperimentAnalysis();
        } catch (IndexOutOfBoundsException e) {
            System.out.print("Experiment failed. This happened because when we run a double hashing function on a table that" +
                    "is a composite number (300) sometimes the data set will cause an infinite loop. The only way to truly" +
                    "prevent this is to use a prime number as the table size for double hashing. This experiment also required" +
                    "me to use the specific table size of 300 for the double hash. My double hashing function should work in " +
                    "most cases. Try rerunning to see a successful experiment.");
        }
        System.out.println();
        System.out.println("********* End Base Experiment *********");

    }

    public static void basicExperimentPlus(){
        GetStatistics newStat = new GetStatistics(true);
        System.out.println("********* Alternate Base Experiment with Different Name Set *********");
        System.out.println("WARNING: Attempting to use a Double Hashing Table with a Composite Number. May Need to Be Rerun.");
        try {
            System.out.println();
            System.out.println("I wanted to see how the numbers might change if I generated the names in a different way");
            newStat.runExperiment(100, 100, false);
            newStat.runExperiment(100, 1000, false);
            newStat.runExperiment(100, 10000, false);
            System.out.println("Experiment Analysis: ");
            System.out.println();
            System.out.println("As you can see, our number are similar in this experiment, although they are are all " +
                    "a bit lower across the board. This shows that the way you generate the disjoint sets of unique " +
                    "names may have at least a modest effect on your results.");
        } catch (IndexOutOfBoundsException e) {
            System.out.println("Experiment failed. This happened because when we run a double hashing function on a table that " +
                    "is a composite number (300) sometimes the data set will cause an infinite loop. The only way to truly " +
                    "prevent this is to use a prime number as the table size for double hashing. This experiment also required " +
                    "me to use the specific table size of 300 for the double hash. My double hashing function should work in " +
                    "most cases. Try rerunning to see a successful experiment.");
        }
        System.out.println();
        System.out.println("********* End Alternate Experiment *********");
    }

    public static void runDoubleHashingExperiment(){
        int[] primeArray = {5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61, 67, 71, 73, 79, 83, 89, 97,
                101, 103, 107, 109, 113, 127, 131, 137, 139, 149, 151, 157, 163, 167, 173, 179, 181, 191, 193, 197, 199,
                211, 223, 227, 229, 233, 239, 241, 251, 257, 263, 269, 271, 277, 281, 283, 293};
        System.out.println("********* Testing different primes in Second Hash Equation for Double Hash Dictionary *********");
        System.out.println();
        System.out.println("I wanted to see if the data would change if I chose different prime numbers for the secondary " +
                "hash function for the Double Hash Dictionary.");
        System.out.println();
        for (int prime : primeArray) {
           System.out.println("Prime: " + prime);
            GetStatistics newStat = new GetStatistics(100, 1.485, prime, false);
            newStat.runExperiment(100,100,false);
       }
        System.out.println("Experiment Analysis:");
        System.out.println();
        System.out.println("For this experiment, I tried every prime from 5 to 293 for a hash table the size of 307. If you " +
                "browse the data, you will see there are not significant spikes anywhere, although there are a few place " +
                "where the collisions rise above 1.5 per search. Although I didn't see any big changes in this data, I did learn " +
                "some important things from doing this part of the experiment.");
        System.out.println();
        System.out.println("1. Even if the number you choose a prime for the secondary hash function that is coprime to the size of the hash " +
                "table, you can still have infinite loops in your secondary hash function. The important thing to find is a prime " +
                "number that *produces* results that are coprime to the size of your original hash table. (Actually, " +
                "this is correct: If you get a result that is coprime, it then gets added to the result from your first " +
                "hash function, which would then allow you to visit every entry on the hash table). I unfortunately did not have " +
                "time to research this idea further, nor could I find more guidance on this topic. This is why for this part of the experiment " +
                "I chose to use a hash table that was of prime size, and in fact, this is why most guidelines for double hashing recommend you " +
                "set your hash table to a prime size. When your hash table is prime, the only factors it has are one and " +
                "itself, so all indices generated by the secondary hash function are guaranteed to be coprime with it.");
        System.out.println();
        System.out.println("2. Fun Fact. Its not generally recommended to use 31 as the prime for your secondary hash " +
                "function as apparently it is used frequently in Java's hashCode() formula so it frequently causes " +
                "collisions. You can see from this page (or at least from the time I ran it) one of the small spikes does " +
                "occur at that number.");
        System.out.println();
        System.out.println("********* End Double Hash Experiment *********");


    }

    public static void printDescription() {
       System.out.println("This program allows you analyze the results of three different experiments.");
    }

    public static void printExperimentAnalysis() {
        System.out.println();
        System.out.println("Experiment Analysis:");
        System.out.println();
        System.out.println("""
                For linear probing, the formula for the number of unsuccessful searches is:\s
                 \

                0.5{ 1+ 1 / [Math.pow(1-lambda), 2] }\s

                where lambda is the load factor of the hashTable. We can then use load factor in this formula: \
                \
                lambda = (number of entries in dictionary) / (number of entries in hash table), \
                \
                to calculate the size that the hashTable needs to be for the given parameters.""");
        System.out.println();
        System.out.println("In the case of our experiment, for the linear hash table, lambda = (2 - sqrt(2))/ 2, " +
                "which is approximately 0.2929, which would lead to a hash table of around size 342.");
        System.out.println();
        System.out.println("""
                For double hashing, the formula for the number of unsuccessful searches is:\s
                 \

                1 + [1 / (1-lambda)]""");
        System.out.println();
        System.out.println("In the case of our experiment, for the double hash table, lambda = 1/3, which would lead to" +
                " a hash table of size 300.");

        System.out.println();
        System.out.println("Between the two experiments, the average number of probes for an unsuccessful search " +
                "remained the same on a per-search basis. For both linear probing and double hashing I am seeing " +
                "a little over 1.5 probes per search. This is to be expected, as the experiment was set up to yield " +
                "on average 1.5 probes per unsuccessful search.");
        System.out.println();
        System.out.println("The standard deviations per search for the 1000 name search were less than the " +
                "100 name experiment. I was getting about 0.15 SD per search for linear probing and 0.09 for double hashing " +
                "in the 100 name search experiment, but this dropped to about 0.09 and 0.03 for linear probing and " +
                "double hashing respectively for the 1000 name experiment. This is to be expected, as increasing the " +
                "sample size often can show a decrease in spread. The more experiments we have, the less likely our " +
                "standard deviation will be affected by outliers. We can see both of these facts " +
                "hold true as we increase our search to 10,000 names. The average number of probes for an unsuccessful search " +
                "is holding at around 1.5 for both hash tables, but the standard deviation has again dropped" +
                " per search for both tables.");
        System.out.println();
    }


}
