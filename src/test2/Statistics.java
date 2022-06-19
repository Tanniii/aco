package test2;

/**
 * Calculate the statistics of the algorithm evolution
 */
public class Statistics {

    /**
     * Environment to be analysed
     */
    private Environment environment;

    public double getBestSoFar() {
        return bestSoFar;
    }

    public int[] getBestTourSoFar() {
        return bestTourSoFar;
    }

    /**
     * The cost of the best so far tour
     */
    private double bestSoFar = Double.MAX_VALUE;

    /**
     * The route of the best so far tour
     */
    private int[] bestTourSoFar;

    /**
     * A swing component to visualize graphically the algorithm evolution
     */
    private Visualizer visualizer;

    /**
     * Current tsp file being solved
     */
    private String tspFile;
    /**
     * Needs an environment and the coordinates of the vertices to be drawn
     *
     * @param environment
     * @param coordinates
     */
    public Statistics(String tspFile, Environment environment, double[][] coordinates) {
        this.environment = environment;
       this.visualizer = new Visualizer(coordinates);
        this.tspFile = tspFile;
    }

    /**
     * For each iteration get the best, the worst and the mean tour cost
     * of all tours constructed by the ants, if a improvement was detected
     * show show the values.
     *
     * @param phase
     */
    public void calculateStatistics(int phase) {
        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;
        double total = 0.0;
        Ant bestAnt = null;
        for(Ant ant : environment.getAnts()) {
            if(ant.getTourCost() < min) {
                min = ant.getTourCost();
                bestAnt = ant;
            }
            if(ant.getTourCost() > max) {
                max = ant.getTourCost();
            }
            total += ant.getTourCost();
        }
        if(min < bestSoFar) {
            bestSoFar = min;
            bestTourSoFar = bestAnt.getTour().clone();
          //  String stats = String.format("%s -> Min(%.1f) Phase(%d) Max(%.1f) Mean(%.1f)\n", tspFile, min, phase, max, (total / environment.getAntPopSize()));
            String message = "[length:" + min + ", phase: "+ phase + ":" + bestTourSoFar[0];
            for(int i = 1; i < bestTourSoFar.length - 1; i++) {
                message += "->" + bestTourSoFar[i];
            }
            message += "]";
            System.out.println(message);
            //visualizer.setStat(stats);
           // visualizer.draw(bestTourSoFar);
            try { Thread.sleep(1000); } catch (Exception ex) {}
        }
    }

    /**
     * End visualization
     */
    public void close() {
        this.visualizer.dispose();
    }

}