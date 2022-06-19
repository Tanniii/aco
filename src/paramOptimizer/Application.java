package paramOptimizer;

import com.sun.tools.javac.Main;
import test2.Parameters;
import test2.Program;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Application {

    public static void main(String[] args) throws Exception {
       Optimizer opti = new Optimizer();
       opti.runOpti();
    }


}
