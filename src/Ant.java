import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.logging.Logger;

public class Ant {

    private static final Logger log = Logger.getLogger(Ant.class.getName());

    private final long id;
    private final List<City> cities;
    private final double[][] distanceMatrix;
    private final MersenneTwisterFast randomGenerator;
    private final int numOfCities;
    private final boolean[] visited;
    private final double a;
    private final double b;
    private final double randomFactor;

    public Ant(long id, AntColonyOptimization aco) {
        this.id = id;
        this.cities = aco.getNodes();
        this.distanceMatrix = aco.getDistanceMatrix();
        this.randomGenerator = new MersenneTwisterFast(System.nanoTime());
        this.a = aco.getAlpha();
        this.b = aco.getBeta();
        this.randomFactor = aco.getRandomFactor();
        this.numOfCities = cities.size();
        visited = new boolean[numOfCities];
    }

    public Callable<Trail> trailWalker(double[][] pheromoneMatrix) {
        return () -> newTrail(pheromoneMatrix);
    }

    public Trail newTrail(double[][] pheromoneMatrix) {
        clear();
        var trail = new Trail(this, cities, distanceMatrix);
        var nodeIndex = randomGenerator.nextInt(numOfCities);
        trail.add(nodeIndex);
        visited[nodeIndex] = true;
        for (int i = 1; i < numOfCities; i++) {
            var currentNodeIndex = trail.getCityIndex(i - 1);
            var nextNodeIndex = nextNode(currentNodeIndex, pheromoneMatrix);
            trail.add(nextNodeIndex);
            visited[nextNodeIndex] = true;
        }

        return trail;
    }

    private int nextNode(int cIndex, double[][] pheromoneMatrix) {
        var randomNumber = randomGenerator.nextDouble();
        if (randomNumber < randomFactor) {
            var randomNodeIndex = randomGenerator.nextInt(numOfCities);
            if (!visited[randomNodeIndex]) {
                return randomNodeIndex;
            }
        }
        var probabilities = calcProbabilities(cIndex, pheromoneMatrix);
        var probabilitiesCum = 0.0;
        for (int i = 0; i < probabilities.length; i++) {
            probabilitiesCum += probabilities[i];
            probabilitiesCum = Math.min(probabilitiesCum, 1.0);
            if (probabilitiesCum >= randomNumber) {
                return i;
            }
        }
        throw new RuntimeException("failed to select next node (randomNumber = " +
                randomNumber + ", probabilitiesCum = " + probabilitiesCum + ")");
    }

    private double[] calcProbabilities(int currentNodeIndex, double[][] pheromoneMatrix) {
        var probabilities = new double[numOfCities];
        var probabilitiesSum = 0.0;
        for (int nextNodeIndex = 0; nextNodeIndex < numOfCities; nextNodeIndex++) {
            if (visited[nextNodeIndex])
                continue;
            var distance = distanceMatrix[currentNodeIndex][nextNodeIndex];
            var desirability = distance <= 0.0 ? 1.0 : (1.0 / distance);
            var pheromones = pheromoneMatrix[currentNodeIndex][nextNodeIndex];
            probabilities[nextNodeIndex] = 
                Math.pow(pheromones, a) * Math.pow(desirability, b);
            probabilitiesSum += probabilities[nextNodeIndex];
        }

        for (int i = 0; i < numOfCities; i++) {
            if (visited[i]) {
                probabilities[i] = 0.0;
            } else {
                probabilities[i] = probabilities[i] / probabilitiesSum;
            }
        }
        return probabilities;
    }

    private void clear() {
        Arrays.fill(visited, false);
    }

    public long getId() {
        return id;
    }
}
