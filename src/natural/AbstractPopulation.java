package natural;

@SuppressWarnings({"WeakerAccess", "unused"})
public abstract class AbstractPopulation {
    protected int generation = 0;

    public void evolveUntilGeneration(int maxGeneration) {
        while (generation < maxGeneration)
            evolve();
    }

    public void evolveUntilGoal(double fitnessGoal) {
        while (getBestFitness() < fitnessGoal)
            evolve();
    }

    public void evolveUntilNoProgress(int maxStaleGenerations) {
        double prev = 0;
        for (int i = 0; i < maxStaleGenerations; i++) {
            evolve();
            double curr = getBestFitness();
            if (curr > prev) i = -1;
            prev = curr;
        }
    }

    public abstract void evolve();

    public abstract long getBestFitness();

    public int getGeneration() {
        return generation;
    }
}
