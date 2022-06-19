package test2;

import java.io.File;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Program {

    private int n;
    private Environment environment;
    private Statistics statistics;

    private Parameters parameters;

    private final static Logger logger = Logger.getLogger(Program.class.getName());
    public static void main(String[] args) throws Exception {

        String tspPath = (new File(".")).getCanonicalPath();
        tspPath = Paths.get(tspPath, "tsp").toAbsolutePath().toString();
        String tspFiles[] = {"a280.tsp"};

        Program app = new Program();
        // Test more simulations
        for(String tspFile : tspFiles) {
            System.out.println("\nProblem: " + tspFile);
            app.startApplication(tspPath, tspFile);
        }
    }

    public  double mainhelper(Parameters parameters) throws Exception {
        this.parameters = parameters;
        String tspPath = (new File(".")).getCanonicalPath();
        tspPath = Paths.get(tspPath, "tsp").toAbsolutePath().toString();
        String tspFiles[] = {"a280.tsp"};

        double best = 0;
        Program app = new Program();
        // Test more simulations
        for(String tspFile : tspFiles) {
            System.out.println("\nProblem: " + tspFile);
            best = app.startApplication(tspPath, tspFile);
        }
        return best;
    }

    // Main part of the algorithm
    public double startApplication(String path, String file) {
       // parameters = new Parameters(0.1,1.35,2.0,0.8, 100, 3000);
        // Create a TSP instance from file with .tsp extension
         environment = new Environment(TspReader.getDistances(path, file));
         statistics = new Statistics(file, environment, TspReader.getCoordinates(path, file));

        // Startup part
        environment.generateNearestNeighborList();
        environment.generateAntPopulation();
        environment.generateEnvironment();

        // Repeat the ants behavior by n times
        int n = 0;
       //int threads =  Runtime.getRuntime().availableProcessors();
      /*  ExecutorService executorService = Executors.newFixedThreadPool(threads);
        for (int i = 0; i < threads; i++) {
            executorService.submit(this::run);
        }
       executorService.shutdown();
        try {
            if(executorService.awaitTermination(1, TimeUnit.MINUTES)) {
                logger.log(Level.INFO,"Best" + "Iteration: " + statistics.getBestSoFar() + "Total: " + Arrays.toString(statistics.getBestTourSoFar()));
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }*/
        //run();
        try { Thread.sleep(3000); } catch (Exception ex) {}
        statistics.close();
        System.out.println("Finished");
        return statistics.getBestSoFar();
    }

    public void run() {
        while(n < parameters.iterationsMax) {
            environment.constructSolutions();
            environment.updatePheromone();
            statistics.calculateStatistics(n);
            System.out.println("it durch :" + n);
            n++;
        }
    }
}