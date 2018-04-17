package natural;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SuppressWarnings({"WeakerAccess", "unused"})
public abstract class AbstractPopulation {
    protected int generation = 0;
    protected final ExecutorService pool;
    protected final int maxThreads;
    protected final int threadWork;

    public AbstractPopulation(int popSize) {
        this(Runtime.getRuntime().availableProcessors(), popSize);
    }

    public AbstractPopulation(int maxThreads, int popSize) {
        if(maxThreads > popSize) maxThreads = popSize;
        this.maxThreads = maxThreads;
        threadWork = popSize / maxThreads + (popSize % maxThreads != 0 ? 1 : 0);
        pool = Executors.newFixedThreadPool(maxThreads);
    }

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
            long curr = getBestFitness();
            if (curr > prev) i = -1;
            prev = curr;
        }
    }

    public void evolve(int times) {
        for (int i = 0; i < times; i++) evolve();
    }

    public abstract void evolve();

    public void evolveUntilGenerationParallel(int maxGeneration) throws InterruptedException {
        while (generation < maxGeneration)
            evolveParallel();
    }

    public void evolveUntilGoalParallel(double fitnessGoal) throws InterruptedException {
        while (getBestFitness() < fitnessGoal)
            evolveParallel();
    }

    public void evolveUntilNoProgressParallel(int maxStaleGenerations) throws InterruptedException {
        double prev = 0;
        for (int i = 0; i < maxStaleGenerations; i++) {
            evolveParallel();
            long curr = getBestFitness();
            if (curr > prev) i = -1;
            prev = curr;
        }
    }

    public void evolveParallel(int times) throws InterruptedException {
        for (int i = 0; i < times; i++) evolveParallel();
    }

    public abstract void evolveParallel() throws InterruptedException;

    public abstract long getBestFitness();

    public int getGeneration() {
        return generation;
    }
}
