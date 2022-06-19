package aco;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import static aco.LoggerClass.loggerStart;

public class App {

    private static final Logger LOGGER = Logger.getLogger(App.class.getName());
    
    public static void main(String[] args) {
        Parameters paramConfig = Configuration.INSTANCE.defaultParamConfig;
        long startTime = System.currentTimeMillis();
        loggerStart();
        Route bestRoute = run(paramConfig);
        double deltaInSeconds = ((System.currentTimeMillis() - startTime) / 1000.0);
        LoggerClass.LOGGER.log(Level.INFO, "Best route found in " + deltaInSeconds + "s" + " with total length " +
                bestRoute.getTotal() + ": " + bestRoute);

    }

    public static Route run(Parameters paramConfig) {
       List<City> cities = loadCities();
        LOGGER.log(Level.INFO, "Problem: " + cities.size() + " cities");
        AntColonyOptimization optimizer = new AntColonyOptimization(cities, paramConfig);
        return optimizer.run();
    }

    public static List<City> loadCities() {
        ArrayList<City> cities = new ArrayList<>();
        try {
            Scanner input = new Scanner(new File(Configuration.INSTANCE.dataDirectory + "a280.tsp"));
            int i = 0;
            while (input.hasNextLine()) {
                String line = input.nextLine();
                if(i > 5 && i <286) {
                    String[] values = line.trim().split("\s+");
                    cities.add(new City(Integer.parseInt(values[0]), Integer.parseInt(values[1]), Integer.parseInt(values[2])));
                }
                i++;
            }
            return cities;
        } catch (FileNotFoundException e) {
            LOGGER.log(Level.SEVERE, "Cant find tsp file " + e.getMessage());
            return cities;
        }

    }
}
