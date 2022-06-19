package test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.IntStream;

public class AntColonyOptimization {

    private double c = 1.0;
    private double alpha = 1.35;
    private double beta = 2.0;
    private double evaporation = 0.01;
    private double Q = 100;
    private double antFactor = 0.8;
    private double randomFactor = 0.0001;
    /*private double c = 1.0;
    private double alpha = 1;
    private double beta = 75;
    private double evaporation = 0.5;
    private double Q = 500;
    private double antFactor = 0.8;
    private double randomFactor = 0.01;*/

    private int maxIterations = 3000;

    private int numberOfCities;
    private int numberOfAnts;
    private double graph[][];
    private double trails[][];
    private List<Ant> ants = new ArrayList<>();
    private Random random = new Random();
    private double probabilities[];

    private int currentIndex;

    private int[] bestTourOrder;
    private double bestTourLength;

    public AntColonyOptimization(int noOfCities) {
        graph = generateRandomMatrix(noOfCities);
        numberOfCities = graph.length;
        numberOfAnts = (int) (numberOfCities * antFactor);

        trails = new double[numberOfCities][numberOfCities];
        probabilities = new double[numberOfCities];
        IntStream.range(0, numberOfAnts)
                .forEach(i -> ants.add(new Ant(numberOfCities)));
    }

    /**
     * Generate initial solution
     */
    public double[][] generateRandomMatrix(int n) {

        double[][] randomMatrix = new double[n][n];
        HashMap<Integer, double[]> coords = new HashMap<>();
        try {
            Scanner input = new Scanner(new File(Configuration.INSTANCE.dataDirectory + "a280.tsp"));
            int i = 0;
            while (input.hasNextLine()) {

                String line = input.nextLine();

                if(i > 5 && i <286) {
                    String[] values = line.trim().split("\s+");
                   // System.out.println(values[1] + ":" + values[2] + ":" + values[0]);
                    coords.put(Integer.parseInt(values[0]), new double[]{Double.parseDouble(values[1]), Double.parseDouble(values[2])});
                }
                i++;
            }
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
        for (int i = 0; i < coords.size(); i++) {
            for (int j = 0; j < coords.size(); j++) {
                if (i==j)
                    randomMatrix[i][j] = 0;
                else {
                    double tmp = Math.sqrt(Math.pow(coords.get(i + 1)[0] - coords.get(j + 1)[0], 2) + Math.pow(coords.get(i + 1)[1] - coords.get(j + 1)[1], 2));
                    randomMatrix[i][j] = tmp;
                }
            }
        }
        return randomMatrix;
    }

    /**
     * Perform ant optimization
     */
    public void startAntOptimization() {
        IntStream.rangeClosed(1, 3)
                .forEach(i -> {
                    System.out.println("Attempt #" + i);
                    solve();
                });
    }

    /**
     * Use this method to run the main logic
     */
    public int[] solve() {
        setupAnts();
        clearTrails();
        IntStream.range(0, maxIterations)
                .forEach(i -> {
                    moveAnts();
                    updateTrails();
                    updateBest();
                });
        System.out.println("Best tour length: " + (bestTourLength - numberOfCities));
        System.out.println("Best tour order: " + Arrays.toString(bestTourOrder));
        return bestTourOrder.clone();
    }

    /**
     * Prepare ants for the simulation
     */
    private void setupAnts() {
        IntStream.range(0, numberOfAnts)
                .forEach(i -> {
                    ants.forEach(ant -> {
                        ant.clear();
                        ant.visitCity(-1, random.nextInt(numberOfCities));
                    });
                });
        currentIndex = 0;
    }

    /**
     * At each iteration, move ants
     */
    private void moveAnts() {
        IntStream.range(currentIndex, numberOfCities - 1)
                .forEach(i -> {
                    ants.forEach(ant -> ant.visitCity(currentIndex, selectNextCity(ant)));
                    currentIndex++;
                });
    }

    /**
     * Select next city for each ant
     */
    private int selectNextCity(Ant ant) {
        int t = random.nextInt(numberOfCities - currentIndex);
        if (random.nextDouble() < randomFactor) {
            OptionalInt cityIndex = IntStream.range(0, numberOfCities)
                    .filter(i -> i == t && !ant.visited(i))
                    .findFirst();
            if (cityIndex.isPresent()) {
                return cityIndex.getAsInt();
            }
        }
        calculateProbabilities(ant);
        double r = random.nextDouble();
        double total = 0;
        for (int i = 0; i < numberOfCities; i++) {
            total += probabilities[i];
            if (total >= r) {
                return i;
            }
        }
        for (int i = 0; i < Configuration.INSTANCE.numberOfCities; i++) {
            if(ant.visited[i] ==false)
                return i;
        }
        throw new RuntimeException("There are no other cities");
    }

    /**
     * Calculate the next city picks probabilites
     */
    public void calculateProbabilities(Ant ant) {
        int i = ant.trail[currentIndex];
        double pheromone = 0.0;
        for (int l = 0; l < numberOfCities; l++) {
            if (!ant.visited(l)) {
                pheromone += Math.pow(trails[i][l], alpha) * Math.pow(1.0 / graph[i][l], beta);
            }
        }
        for (int j = 0; j < numberOfCities; j++) {
            if (ant.visited(j)) {
                probabilities[j] = 0.0;
            } else {
                double numerator = Math.pow(trails[i][j], alpha) * Math.pow(1.0 / graph[i][j], beta);
                probabilities[j] = numerator / pheromone;
            }
        }
    }

    /**
     * Update trails that ants used
     */
    private void updateTrails() {
        for (int i = 0; i < numberOfCities; i++) {
            for (int j = 0; j < numberOfCities; j++) {
                trails[i][j] *= evaporation;
            }
        }
        for (Ant a : ants) {
            double contribution = Q / a.trailLength(graph);
            for (int i = 0; i < numberOfCities - 1; i++) {
                trails[a.trail[i]][a.trail[i + 1]] += contribution;
            }
            trails[a.trail[numberOfCities - 1]][a.trail[0]] += contribution;
        }
    }

    /**
     * Update the best solution
     */
    private void updateBest() {
        if (bestTourOrder == null) {
            bestTourOrder = ants.get(0).trail;
            bestTourLength = ants.get(0)
                    .trailLength(graph);
        }
        for (Ant a : ants) {
            if (a.trailLength(graph) < bestTourLength) {
                bestTourLength = a.trailLength(graph);
                bestTourOrder = a.trail.clone();
            }
        }
    }

    /**
     * Clear trails after simulation
     */
    private void clearTrails() {
        IntStream.range(0, numberOfCities)
                .forEach(i -> {
                    IntStream.range(0, numberOfCities)
                            .forEach(j -> trails[i][j] = c);
                });
    }

    public double getC() {
        return c;
    }

    public void setC(double c) {
        this.c = c;
    }

    public double getAlpha() {
        return alpha;
    }

    public void setAlpha(double alpha) {
        this.alpha = alpha;
    }

    public double getBeta() {
        return beta;
    }

    public void setBeta(double beta) {
        this.beta = beta;
    }

    public double getEvaporation() {
        return evaporation;
    }

    public void setEvaporation(double evaporation) {
        this.evaporation = evaporation;
    }

    public double getQ() {
        return Q;
    }

    public void setQ(double q) {
        Q = q;
    }

    public double getAntFactor() {
        return antFactor;
    }

    public void setAntFactor(double antFactor) {
        this.antFactor = antFactor;
    }

    public double getRandomFactor() {
        return randomFactor;
    }

    public void setRandomFactor(double randomFactor) {
        this.randomFactor = randomFactor;
    }
}