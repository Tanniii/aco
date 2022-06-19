package aco;

import java.io.IOException;
import java.util.logging.*;

public class LoggerClass {

    static Logger LOGGER;
    public static void loggerStart() {

        LOGGER = Logger.getLogger("MyLog");
        LOGGER.setLevel(Level.ALL);
        try {
            FileHandler fileHandler = new FileHandler("BruteForce.log");
            fileHandler.setFormatter(new SimpleFormatter());

            fileHandler.setLevel(Level.ALL);
            LOGGER.addHandler(fileHandler);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}