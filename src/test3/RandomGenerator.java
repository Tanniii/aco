package test3;

public interface RandomGenerator {
    
    RandomGenerator cloneWithSeed(long seed);

    double nextDouble();
    int nextInt(int n);
}
