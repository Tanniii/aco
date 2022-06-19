public class City {
    private final int name;
    private final double locationX;
    private final double locationY;

    public City(int name, double x, double y) {
        this.name = name;
        this.locationX = x;
        this.locationY = y;
    }

    public double distance(City other) {
        double h = this.getLocationX() - other.getLocationX();
        var v = this.getLocationY() - other.getLocationY();
        return Math.sqrt(Math.pow(h, 2) + Math.pow(v, 2));
    }

    public double getLocationX() {
        return locationX;
    }

    public double getLocationY() {
        return locationY;
    }

    public int getName() {
        return name;
    }
}