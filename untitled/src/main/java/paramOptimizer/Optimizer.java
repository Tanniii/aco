package paramOptimizer;


import aco.AntColonyOptimization;
import aco.City;
import aco.Parameters;
import aco.Route;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.fasterxml.jackson.databind.ObjectMapper;


import static aco.App.loadCities;

public class Optimizer {

    private static final Logger log = Logger.getLogger(Optimizer.class.getName());
    private static double bestSoFar;
    private static double[] bestVal;
    public void runOpti() throws Exception {
        List<City> cities = loadCities();
        bestSoFar = Double.MAX_VALUE;
        bestVal = new double[] {0,0,0,0};
        long counter =0l;
        int threads =  Runtime.getRuntime().availableProcessors();
        ExecutorService executorService = Executors.newFixedThreadPool(threads);
        for (double i = Range.aMin; i <= Range.aMax;) {
            for (double j = Range.bMin; j <= Range.bMax;) {
                for (double k = Range.evapMin; k <= Range.evapMax;) {
                    for (double l = Range.antPopSizeMin; l <= Range.antPopSizeMax;) {
                        final Route[] best = new Route[1];
                        AntColonyOptimization aco = new AntColonyOptimization(cities, new Parameters(1000, l, 1.0, k, 100, i, j, 0.0001,1e-3));

                       executorService.submit(() -> best[0] = aco.run());
                        executorService.awaitTermination(100, TimeUnit.SECONDS);
                       if(best[0].getTotal()< bestSoFar) {
                           bestSoFar = best[0].getTotal();
                           bestVal = new double[]{i, j, k, l};
                       }

                        l=+0.1;
                    }
                    k =+ 0.05;
                }
                j =+0.1;
            }
            i =+0.1;
        }
        log.log(Level.INFO, "Best trail length: " + bestSoFar + " best Params: a " + bestVal[0] + ", b " +  bestVal[1] + ", evaporation " +  bestVal[2] + ", antfactor " +  bestVal[3]);

        ObjectMapper mapper = new ObjectMapper();

        BestParams bestParams = setBestParam();

        try {

            // Java objects to JSON file
            mapper.writeValue(new File("params.json"), bestParams);

            // Java objects to JSON string - compact-print
            String jsonString = mapper.writeValueAsString(bestParams);

            System.out.println(jsonString);

            // Java objects to JSON string - pretty-print
            String jsonInString2 = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(bestParams);

            System.out.println(jsonInString2);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private BestParams setBestParam() {
        BestParams bestParams = new BestParams();

        bestParams.setAlpha(bestVal[0]);
        bestParams.setBeta(bestVal[1]);
        bestParams.setEvaporation(bestVal[2]);
        bestParams.setAntFactor(bestVal[3]);
        bestParams.setBestTrailLength(bestSoFar);

        return bestParams;
    }
    /*private void runSolving(double i, double j, double k, double l) throws Exception {
        Program p = new Program();
        double best = p.mainhelper(new Parameters(k, i, j, l, 100, 10));
        if(best < bestSoFar) {
            bestSoFar = best;
            bestVal = new double[]{i, j, k, l};
        }
    }*/
}
