package test3;

import test3.parmeter.ParameterConfiguration;
import test3.parmeter.PheromoneMatrixUpdateMethod;

public enum Configuration {
    INSTANCE;

    public final String loggingPropertiesFile = "logging.properties";

    public final int threads = Runtime.getRuntime().availableProcessors();

    public final DistanceFunction distanceFunc = new EuclideanDistance();
    public final RandomGenerator randomGenerator = new MersenneTwisterFast(System.nanoTime());

    // Configurable through command line arguments

    public final ParameterConfiguration defaultParamConfig =
            new ParameterConfiguration(
                    "a280.tsp",
                    3000,
                    0.8,
                    1.0,
                    0.006,
                    100,
                    1.35,
                    2.0,
                    0.0001,
                    1e-3,
                    PheromoneMatrixUpdateMethod.BEST_TRAIL
            );
}
