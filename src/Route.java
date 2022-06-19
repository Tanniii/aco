import java.util.ArrayList;
import java.util.List;


public class Route {
    
    private final long id;
    private final List<City> cities = new ArrayList<>();

    public Route(long id, List<City> cities) {
        this.id = id;
        this.cities.addAll(cities);
    }

    public double getTotal() {
        var size = cities.size();
        var totalDistance = 0D;

        // Cycle through nodes, (i = size) == (i = 0)
        for (int i = 0; i < size; i++) {
            var current = cities.get(i);
            var next = cities.get((i + 1) % size);
            var distance = current.distance(next);
            totalDistance += distance;
        }

        return totalDistance;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < cities.size(); i++) {
            sb.append(cities.get(i).getName() + "->");
        }
        sb.append("]");
        return sb.toString();
    }

}