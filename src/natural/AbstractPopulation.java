package natural;

import natural.interfaces.Action;

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
        if (maxThreads > popSize) maxThreads = popSize;
        this.maxThreads = maxThreads;
        // If work doesn't split evenly to threads, add 1 to all threads
        // Last thread end with less work (and extreme cases a lot of threads end without work)
        // But it's a damn cheap and easy way to divide the work
        threadWork = popSize / maxThreads + Math.min(popSize % maxThreads, 1);
        pool = Executors.newFixedThreadPool(maxThreads);
    }

    public void evolveUntilGeneration(int maxGeneration) throws Exception {
        evolveUntilGeneration(maxGeneration, () -> {
        });
    }

    public void evolveUntilGeneration(int maxGeneration, Action action) throws Exception {
        while (generation < maxGeneration) {
            evolve();
            action.act();
        }
    }

    public void evolveUntilGoal(double fitnessGoal) throws Exception {
        evolveUntilGoal(fitnessGoal, () -> {
        });
    }

    public void evolveUntilGoal(double fitnessGoal, Action action) throws Exception {
        while (getBestFitness() < fitnessGoal) {
            evolve();
            action.act();
        }
    }

    public void evolveUntilNoProgress(int maxStaleGenerations) throws Exception {
        evolveUntilNoProgress(maxStaleGenerations, () -> {
        });
    }

    public void evolveUntilNoProgress(int maxStaleGenerations, Action action) throws Exception {
        double prev = 0;
        for (int i = 0; i < maxStaleGenerations; i++) {
            evolve();
            action.act();
            long curr = getBestFitness();
            if (curr > prev) i = -1;
            prev = curr;
        }
    }

    public void evolve(int times) throws Exception {
        evolve(times, () -> {
        });
    }

    public void evolve(int times, Action action) throws Exception {
        for (int i = 0; i < times; i++) {
            evolve();
            action.act();
        }
    }

    public abstract void evolve() throws Exception;

    public void evolveUntilGenerationParallel(int maxGeneration) throws Exception {
        evolveUntilGenerationParallel(maxGeneration, () -> {
        });
    }

    public void evolveUntilGenerationParallel(int maxGeneration, Action action) throws Exception {
        while (generation < maxGeneration) {
            evolveParallel();
            action.act();
        }
    }

    public void evolveUntilGoalParallel(double fitnessGoal) throws Exception {
        evolveUntilGoalParallel(fitnessGoal, () -> {
        });
    }

    public void evolveUntilGoalParallel(double fitnessGoal, Action action) throws Exception {
        while (getBestFitness() < fitnessGoal) {
            evolveParallel();
            action.act();
        }
    }

    public void evolveUntilNoProgressParallel(int maxStaleGenerations) throws Exception {
        evolveUntilNoProgressParallel(maxStaleGenerations, () -> {});
    }

    public void evolveUntilNoProgressParallel(int maxStaleGenerations, Action action) throws Exception {
        double prev = 0;
        for (int i = 0; i < maxStaleGenerations; i++) {
            evolveParallel();
            action.act();
            long curr = getBestFitness();
            if (curr > prev) i = -1;
            prev = curr;
        }
    }

    public void evolveParallel(int times) throws Exception {
        evolveParallel(times, () -> { });
    }

    public void evolveParallel(int times, Action action) throws Exception {
        for (int i = 0; i < times; i++) {
            evolveParallel();
            action.act();
        }
    }

    public abstract void evolveParallel() throws InterruptedException, Exception;

    public abstract long getBestFitness();

    public int getGeneration() {
        return generation;
    }

    public abstract AbstractIndividual getBest();

    public int getMaxThreads() {
        return maxThreads;
    }

    public int getThreadWork() {
        return threadWork;
    }
}
