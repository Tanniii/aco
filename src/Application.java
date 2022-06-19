import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Application {
    public static void main(String... args) {
        AntColonyOptimization aco = new AntColonyOptimization(loadCities());
        aco.run();
    }

    private static List<City> loadCities() {
        ArrayList<City> cities = new ArrayList<>();
        try {
            Scanner input = new Scanner(new File(Configuration.INSTANCE.dataDirectory + "a280.tsp"));
            int i = 0;
            while (input.hasNextLine()) {

                String line = input.nextLine();

                if(i > 5 && i <286) {
                    String[] values = line.trim().split("\s+");
                    cities.add(new City(Integer.parseInt(values[0]), Integer.parseInt(values[1]), values[2]));
                }
                i++;
            }
            return cities;
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
            return cities;
        }

    }
}