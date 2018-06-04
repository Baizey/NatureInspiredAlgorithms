package natural;

import natural.interfaces.Action;
import natural.interfaces.PreCalc;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SuppressWarnings({"WeakerAccess", "unused"})
public abstract class AbstractPopulation {
    protected long generation = 0;
    protected final ExecutorService pool;
    protected final int maxThreads;
    protected final int threadWork;
    protected HashMap<String, Object> memory = new HashMap<>();
    protected final PreCalc preCalc;
    protected final int popSize;

    public AbstractPopulation(int popSize, PreCalc preCalc) {
        this(Runtime.getRuntime().availableProcessors(), popSize, preCalc);
    }

    public AbstractPopulation(int maxThreads, int popSize, PreCalc preCalc) {
        maxThreads = Math.max(maxThreads, 1);
        if (maxThreads > popSize) maxThreads = popSize;
        this.maxThreads = maxThreads;
        this.popSize = popSize;
        // If work doesn't split evenly to threads, add 1 to all threads
        // Last thread end with less work (and extreme cases a lot of threads end without work)
        // But it's a damn cheap and easy way to divide the work
        // Often this error wont matter much either, as you'll usually have <30 threads and >200 population
        // Which at worst misplaces (threads - 1) individuals too little for one thread
        this.threadWork = popSize / maxThreads + Math.min(popSize % maxThreads, 1);
        this.pool = Executors.newFixedThreadPool(maxThreads);
        this.preCalc = preCalc;
    }

    public void evolveUntilGeneration(long maxGeneration) throws Exception {
        evolveUntilGeneration(maxGeneration, () -> {
        });
    }

    public void evolveUntilGeneration(long maxGeneration, Action action) throws Exception {
        while (generation < maxGeneration) {
            evolve();
            action.act();
        }
    }

    public void evolveUntilGoal(long fitnessGoal) throws Exception {
        evolveUntilGoal(fitnessGoal, () -> {
        });
    }

    public void evolveUntilGoal(long fitnessGoal, Action action) throws Exception {
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
        long prev = 0;
        int counter = 0;
        while (counter < maxStaleGenerations) {
            evolve();
            action.act();
            long curr = getBestFitness();
            if (curr > prev) counter = 0;
            else counter++;
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

    public void evolveUntilGenerationParallel(long maxGeneration) throws Exception {
        evolveUntilGenerationParallel(maxGeneration, () -> {
        });
    }

    public void evolveUntilGenerationParallel(long maxGeneration, Action action) throws Exception {
        while (generation < maxGeneration) {
            evolveParallel();
            action.act();
        }
    }

    public void evolveUntilGoalParallel(long fitnessGoal) throws Exception {
        evolveUntilGoalParallel(fitnessGoal, () -> {
        });
    }

    public void evolveUntilGoalParallel(long fitnessGoal, Action action) throws Exception {
        while (getBestFitness() < fitnessGoal) {
            evolveParallel();
            action.act();
        }
    }

    public void evolveUntilNoProgressParallel(int maxStaleGenerations) throws Exception {
        evolveUntilNoProgressParallel(maxStaleGenerations, () -> {
        });
    }

    public void evolveUntilNoProgressParallel(int maxStaleGenerations, Action action) throws Exception {
        long prev = 0;
        int counter = 0;
        while (counter < maxStaleGenerations) {
            evolveParallel();
            action.act();
            long curr = getBestFitness();
            if (curr > prev) counter = 0;
            else counter++;
            prev = curr;
        }
    }

    public void evolveParallel(int times) throws Exception {
        evolveParallel(times, () -> {
        });
    }

    public void evolveParallel(int times, Action action) throws Exception {
        for (int i = 0; i < times; i++) {
            evolveParallel();
            action.act();
        }
    }

    public abstract void evolve() throws Exception;

    public abstract void evolveParallel() throws Exception;

    public abstract AbstractIndividual getBest();

    public abstract long getBestFitness();

    public abstract double getMeanFitness();

    public abstract void copyPopulation(AbstractPopulation other);

    public abstract AbstractIndividual[] getPopulation();

    public abstract AbstractIndividual getIndividual(int index);

    public long getGeneration() {
        return generation;
    }

    public int getMaxThreads() {
        return maxThreads;
    }

    public int getThreadWork() {
        return threadWork;
    }

    public ExecutorService getPool() {
        return pool;
    }

    public int getPopulationSize(){
        return popSize;
    }
}
