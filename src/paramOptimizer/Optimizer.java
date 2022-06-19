package paramOptimizer;


import aco.AntColonyOptimization;
import aco.City;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static aco.App.loadCities;

public class Optimizer {
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
                        final double a = i;
                        final double b = j;
                        final double c = k;
                        final double d = l;
                        counter++;
                       // new AntColonyOptimization(cities);
                       // executorService.submit(() -> {
                            try {
                                //runSolving(a, b, c, d);
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                      //  });

                        l=+0.1;
                    }

                    k =+ 0.05;
                }
                j =+0.1;
            }
            i =+0.1;
        }
        System.out.println(bestSoFar + "->" + bestVal);
        System.out.println(counter);
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
