public class Main {
    public static void main(String[] args) {
        GetStatistics newStat = new GetStatistics();
        newStat.runExperiment(100,100);
        System.out.println();
        newStat.runExperiment(100,1000);
    }
}
