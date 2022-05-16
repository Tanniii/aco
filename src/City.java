public class City {
        private final int x;
        private final int y;
        private final String name;

    public City(int x, int y, String name) {
        this.x = x;
        this.y = y;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public double degree2rad(double degree) {
        return degree * (Math.PI / 180D);
    }

    public double distanceBetweenTwoCities(City city) {
        double degreeLatitude = degree2rad(city.y - y);
        double degreeLongitude = degree2rad(city.x - x);
        double a = Math.sin(degreeLatitude / 2) * Math.sin(degreeLatitude / 2) +
                Math.cos(degree2rad(this.y)) * Math.cos(degree2rad(city.y)) *
                        Math.sin(degreeLongitude / 2) * Math.sin(degreeLongitude / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
       // return Configuration.INSTANCE.r * c;
    return c;
    }
}
