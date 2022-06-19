import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;


public class AntColonyOptimization {

    private static final Logger log = Logger.getLogger(AntColonyOptimization.class.getName());
    private final Parameters paramConfig;
    private final ExecutorService executor;
    private final double[][] distanceMatrix;
    private final double[][] pheromoneMatrix;
    private final List<City> cities;
    private final List<Callable<Trail>> antPossibleSol;
    private Trail bestTrail;
    private double bestTrailLength;

    public AntColonyOptimization(List<City> cities, Parameters paramConfig) {
        bestTrail = null;
        bestTrailLength = Double.MAX_VALUE;
        this.cities = cities;
        this.paramConfig = paramConfig;
        executor = Executors.newFixedThreadPool(Configuration.INSTANCE.threads);
        antPossibleSol = new ArrayList<>();

        distanceMatrix = generateDistanceMatrix(cities);
        pheromoneMatrix = new double[cities.size()][cities.size()];
    }

    private double[][] generateDistanceMatrix(List<City> cities) {
        var numOfNodes = cities.size();
        var distanceMatrix = new double[numOfNodes][numOfNodes];

        for (int i = 0; i < numOfNodes; i++) {
            for (int j = 0; j < numOfNodes; j++) {
                if (i == j) {
                    distanceMatrix[i][j] = 0;
                } else {
                    City from = cities.get(i);
                    City to = cities.get(j);
                    distanceMatrix[i][j] = from.distance(to);
                }
            }
        }

        return distanceMatrix;
    }


    private void setupAnts() {
        var numOfAnts = (int) (paramConfig.antsPerNode() * cities.size());

        antPossibleSol.clear();
        for (int i = 0; i < numOfAnts; i++) {
            antPossibleSol.add(
                    new Ant(i,this).trailWalker(pheromoneMatrix));
        }
    }


    private List<Trail> searchAntSolutions() {
        var trails = new ArrayList<Trail>();
        try {
            for (var future : executor.invokeAll(antPossibleSol)) {
                trails.add(future.get());
            }
        } catch (InterruptedException | ExecutionException e) {
            log.log(Level.SEVERE, "Error while execution: " + e.getMessage());
        }

        return trails;
    }

    private void initPheromoneMatrix() {
        log.log(Level.CONFIG, "Initialize pheromone matrix with " + paramConfig.initialPheromoneValue());
        for (int i = 0; i < cities.size(); i++) {
            for (int j = 0; j < cities.size(); j++) {
                pheromoneMatrix[i][j] = paramConfig.initialPheromoneValue();
            }
        }
    }

    private void updatePheromoneMatrix(List<Trail> trails) {
        // Evaporate the values
        for (int i = 0; i < cities.size(); i++) {
            for (int j = 0; j < cities.size(); j++) {
                pheromoneMatrix[i][j] *= (1.0 - paramConfig.evaporation());
            }
        }
                for (Trail trail : trails) {
                    updatePheromoneMatrixForTrail(trail);
                }

    }

    private void updatePheromoneMatrixForTrail(Trail trail) {
        var contribution = paramConfig.q() / trail.length();
        log.log(Level.FINEST, "Add contribution for trail of ant " + trail.getAnt().getId() +
                " to pheromone matrix: " + contribution);
        for (int i = 0; i < cities.size() - 1; i++) {
            pheromoneMatrix[trail.getCityIndex(i)][trail.getCityIndex(i + 1)] += contribution;
        }
        pheromoneMatrix[trail.getCityIndex(cities.size() - 1)][trail.getCityIndex(0)] += contribution;
    }

    private void updateBestSolution(List<Trail> trails) {
        var bestTrail = findBestTrail(trails);
        if (bestTrail.length() < bestTrailLength) {
            log.log(Level.INFO, "Update bestTrail with length " + bestTrail.length() + ": " +
                    bestTrail.getRoute(bestTrail.getAnt().getId()));
            this.bestTrail = bestTrail;
            this.bestTrailLength = bestTrail.length();
        }
    }

    private double calculateDivergence(List<Trail> trails) {
        var trailLen = 0.0;
        for (var trail : trails) {
            trailLen += trail.length();
        }
        return 1.0 - (this.bestTrailLength / (trailLen / trails.size()));
    }

    private Trail findBestTrail(List<Trail> trails) {
        Trail bestTrail = trails.get(0);
        var bestTrailLength = bestTrail.length();
        for (int i = 1; i < trails.size(); i++) {
            if (trails.get(i).length() < bestTrailLength) {
                bestTrail = trails.get(i);
                bestTrailLength = bestTrail.length();
            }
        }

       // log.log(Level.INFO, "Best trail for this iteration with length " + bestTrailLength +
         //       " is from ant with id " + bestTrail.getAnt().getId() + ": " +
           //     bestTrail.getRoute(bestTrail.getAnt().getId()));
        return bestTrail;
    }

    public Route run() {
        setupAnts();
        initPheromoneMatrix();

        for (int i = 0; i < paramConfig.maxIterations(); i++) {
            List<Trail> trails = searchAntSolutions();
            updateBestSolution(trails);
            updatePheromoneMatrix(trails);
            double divergence = calculateDivergence(trails);
            //log.log(Level.INFO, "Iteration: " + i + ", Current best trail length: " + bestTrail.length() +
                //    ", divergence: " + divergence);
            if (divergence < paramConfig.divergenceToTerminate()) {
                //log.log(Level.INFO, "Break loop after " + i + " iterations because divergence of " +
                  //      paramConfig.divergenceToTerminate() + " was reached (divergence = " + divergence + ")");
                break;
            }
        }

        log.log(Level.INFO, "Algorithm ended because max iterations (" + paramConfig.maxIterations() + ")" +
                " where reached");
        executor.shutdownNow();
        return bestTrail.getRoute(0);
    }

    // Getter and setter

    public List<City> getNodes() {
        return cities;
    }

    public double[][] getDistanceMatrix() {
        return distanceMatrix;
    }

    public MersenneTwisterFast getRandomGenerator() {
        return Configuration.INSTANCE.randomGenerator;
    }

    public double getAlpha() {
        return paramConfig.alpha();
    }

    public double getBeta() {
        return paramConfig.beta();
    }

    public double getRandomFactor() {
        return paramConfig.randomFactor();
    }
}
