package natural;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@SuppressWarnings({"unused", "WeakerAccess"})
public class Islands {

    private ExecutorService pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    private final AbstractPopulation[] islands;
    private final ConvergenceInterface convergence;
    private final EvolutionStepInterface evolutionStep;
    private final int timeout;

    public Islands(
            ConvergenceInterface convergence,
            EvolutionStepInterface evolutionStep,
            AbstractPopulation... populations
    ) {
        this(convergence,evolutionStep, 100, populations);
    }

    public Islands(
            ConvergenceInterface convergence,
            EvolutionStepInterface evolutionStep,
            int timeout,
            AbstractPopulation... populations
    ) {
        this.timeout = timeout;
        this.islands = populations;
        this.convergence = convergence;
        this.evolutionStep = evolutionStep;
    }

    public void evolve() throws InterruptedException {
        for (AbstractPopulation island : islands)
            pool.submit(() -> evolutionStep.evolve(island));
        pool.awaitTermination(timeout, TimeUnit.SECONDS);
        convergence.merge(islands);
    }
}
