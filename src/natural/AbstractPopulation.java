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
        // If work doesn't split evenly to threads, add 1 to all threads
        // Last thread end with less work (and extreme cases a lot of threads end without work)
        // But it's a damn cheap and easy way to divide the work
        threadWork = popSize / maxThreads + Math.min(popSize % maxThreads, 1);
        pool = Executors.newFixedThreadPool(maxThreads);
    }

    public void evolveUntilGeneration(int maxGeneration) throws InterruptedException {
        evolveUntilGeneration(maxGeneration, a -> {});
    }
    public void evolveUntilGeneration(int maxGeneration, Action action) throws InterruptedException {
        while (generation < maxGeneration){
            evolve();
            action.act(this);
        }
    }

    public void evolveUntilGoal(double fitnessGoal) throws InterruptedException { evolveUntilGoal(fitnessGoal, a -> {}); }
    public void evolveUntilGoal(double fitnessGoal, Action action) throws InterruptedException {
        while (getBestFitness() < fitnessGoal) {
            evolve();
            action.act(this);
        }
    }

    public void evolveUntilNoProgress(int maxStaleGenerations) throws InterruptedException { evolveUntilNoProgress(maxStaleGenerations, a -> {}); }
    public void evolveUntilNoProgress(int maxStaleGenerations, Action action) throws InterruptedException {
        double prev = 0;
        for (int i = 0; i < maxStaleGenerations; i++) {
            evolve();
            action.act(this);
            long curr = getBestFitness();
            if (curr > prev) i = -1;
            prev = curr;
        }
    }

    public void evolve(int times) throws InterruptedException { evolve(times, a -> {});}
    public void evolve(int times, Action action) throws InterruptedException {
        for (int i = 0; i < times; i++) {
            evolve();
            action.act(this);
        }
    }

    public abstract void evolve();

    public void evolveUntilGenerationParallel(int maxGeneration) throws InterruptedException { evolveUntilGenerationParallel(maxGeneration, a -> {}); }
    public void evolveUntilGenerationParallel(int maxGeneration, Action action) throws InterruptedException {
        while (generation < maxGeneration){
            evolveParallel();
            action.act(this);
        }
    }

    public void evolveUntilGoalParallel(double fitnessGoal) throws InterruptedException { evolveUntilGoalParallel(fitnessGoal, a -> {}); }
    public void evolveUntilGoalParallel(double fitnessGoal, Action action) throws InterruptedException {
        while (getBestFitness() < fitnessGoal){
            evolveParallel();
            action.act(this);
        }
    }

    public void evolveUntilNoProgressParallel(int maxStaleGenerations) throws InterruptedException { evolveUntilNoProgressParallel(maxStaleGenerations, a -> {}); }
    public void evolveUntilNoProgressParallel(int maxStaleGenerations, Action action) throws InterruptedException {
        double prev = 0;
        for (int i = 0; i < maxStaleGenerations; i++) {
            evolveParallel();
            action.act(this);
            long curr = getBestFitness();
            if (curr > prev) i = -1;
            prev = curr;
        }
    }

    public void evolveParallel(int times) throws InterruptedException { evolveParallel(times, a -> {}); }
    public void evolveParallel(int times, Action action) throws InterruptedException {
        for (int i = 0; i < times; i++) {
            evolveParallel();
            action.act(this);
        }
    }

    public abstract void evolveParallel() throws InterruptedException;

    public abstract long getBestFitness();

    public int getGeneration() {
        return generation;
    }

    public abstract AbstractIndividual getBest();
}
