package natural;

import lsm.helpers.IO.write.text.console.Note;

@SuppressWarnings({"WeakerAccess", "unused"})
public abstract class AbstractPopulation {
    protected int generation = 0;

    public void evolveUntilGeneration(int maxGeneration) {
        while (generation < maxGeneration)
            evolve();
    }

    public void evolveUntilGoal(double fitnessGoal) {
        long old = Long.MIN_VALUE;
        while (getBestFitness() < fitnessGoal) {
            evolve();
            if(old < getBestFitness()) {
                Note.writenl(getBestFitness());
                old = getBestFitness();
            }
        }
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
