public class Main {
    public static void main(String[] args) {
        GetStatistics newStat = new GetStatistics();
        newStat.runExperiment(100,100);
        System.out.println();
        GetStatistics newStat2 = new GetStatistics(100, 1000, 1.5);
        newStat2.runExperiment(100,1000);
    }
}
