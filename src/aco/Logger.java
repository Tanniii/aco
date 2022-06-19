package aco;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.SimpleFormatter;

public class Logger {

    public static void loggerStart() {
        try {
            FileHandler fh = new FileHandler("BruteForce.log");
           // AntColonyOptimization.logger.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);
           // AntColonyOptimization.logger.info("best cost:" + AntColonyOptimization.);
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (
                IOException e) {
            e.printStackTrace();
        }
    }
}
