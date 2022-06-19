import java.util.ArrayList;
import java.util.List;

public class Trail {

    private final Ant ant;
    private final List<City> cities;
    private final double[][] distanceMatrix;
    private final int[] trail;
    private final int numOfNodes;
    private int currentIndex = 0;

    public Trail(Ant ant, List<City> cities, double[][] distanceMatrix) {
        this.ant = ant;
        this.cities = cities;
        this.distanceMatrix = distanceMatrix;
        this.numOfNodes = cities.size();
        this.trail = new int[numOfNodes];
    }

    public void add(int index) {
        trail[currentIndex] = index;
        currentIndex++;
    }

    public int getCityIndex(int trailIndex) {
        return trail[trailIndex];
    }

    public double length() {
        var length = distanceMatrix[trail[numOfNodes - 1]][trail[0]];
        for (int i = 0; i < numOfNodes - 1; i++) {
            length += distanceMatrix[trail[i]][trail[i + 1]];
        }
        return length;
    }

    public Route getRoute(long routeId) {
        var nodes = new ArrayList<City>();
        for (var nodeIndex : trail) {
            nodes.add(this.cities.get(nodeIndex));
        }

        return new Route(routeId, nodes);
    }

    public Ant getAnt() {
        return ant;
    }
}
