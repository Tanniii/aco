package aco;

public enum Configuration {
    INSTANCE;

    public final int threads = Runtime.getRuntime().availableProcessors();
    public final MersenneTwisterFast randomGenerator = new MersenneTwisterFast(System.nanoTime());
    public final String userDirectory = System.getProperty("user.dir");
    public final String fileSeparator = System.getProperty("file.separator");
    public final String dataDirectory = userDirectory + fileSeparator + "tsp" + fileSeparator;

    public final Parameters defaultParamConfig =
            new Parameters(
                    3, //000,
                    0.8,
                    1.0,
                    0.01,
                    100,
                    1.35,
                    2.0,
                    0.0001,
                    1e-3
            );
}
