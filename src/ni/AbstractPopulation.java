package ni;

@SuppressWarnings({"WeakerAccess", "unused"})
public abstract class AbstractPopulation {
    protected int generation = 0;

    public int evolve(int fitnessGoal, int maxAttempts) {
        while (getBestFitness() < fitnessGoal && generation < maxAttempts)
            evolve();
        return generation;
    }

    public int evolve(int maxStaleGenerations) {
        for (int i = 0, prev = 0; i < maxStaleGenerations; i++) {
            evolve();
            int curr = getBestFitness();
            if (curr > prev)
                i = -1;
            prev = curr;
        }
        return generation;
    }

    public abstract void evolve();

    public abstract int getBestFitness();

    public int getGeneration(){
        return generation;
    }
}
