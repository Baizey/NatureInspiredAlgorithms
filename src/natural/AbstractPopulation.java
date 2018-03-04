package natural;

@SuppressWarnings({"WeakerAccess", "unused"})
public abstract class AbstractPopulation {
    protected int generation = 0;

    public int evolveUntilGeneration(int maxGeneration) {
        while (generation < maxGeneration)
            evolve();
        return generation;
    }

    public int evolveUntilGoal(int fitnessGoal) {
        while (getBestFitness() < fitnessGoal)
            evolve();
        return generation;
    }

    public int evolveUntilNoProgress(int maxStaleGenerations) {
        double prev = 0;
        for (int i = 0; i < maxStaleGenerations; i++) {
            evolve();
            double curr = getBestFitness();
            if (curr > prev)
                i = -1;
            prev = curr;
        }
        return generation;
    }

    public abstract void evolve();

    public abstract double getBestFitness();

    public int getGeneration() {
        return generation;
    }
}
