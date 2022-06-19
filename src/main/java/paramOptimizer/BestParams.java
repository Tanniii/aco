package paramOptimizer;

public class BestParams {
    private double alpha;
    private double beta;
    private double evaporation;
    private double antFactor;
    private double bestTrailLength;

    public void setAlpha(double alpha) {
        this.alpha = alpha;
    }

    public void setBeta(double beta) {
        this.beta = beta;
    }

    public void setEvaporation(double evaporation) {
        this.evaporation = evaporation;
    }

    public void setAntFactor(double antFactor) {
        this.antFactor = antFactor;
    }

    public void setBestTrailLength(double bestTrailLength) {
        this.bestTrailLength = bestTrailLength;
    }
}
