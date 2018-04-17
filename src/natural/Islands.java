package natural;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SuppressWarnings({"unused", "WeakerAccess"})
public class Islands {

    private final ExecutorService pool;
    private final AbstractPopulation[] islands;
    private final ConvergenceInterface convergence;
    private final EvolutionStepInterface evolutionStep;

    public Islands(
            ConvergenceInterface convergence,
            EvolutionStepInterface evolutionStep,
            AbstractPopulation... populations
    ) {
        pool = Executors.newFixedThreadPool(populations.length);
        this.islands = populations;
        this.convergence = convergence;
        this.evolutionStep = evolutionStep;
    }

    public void evolve() throws InterruptedException {
        CountDownLatch counter = new CountDownLatch(islands.length);
        for (AbstractPopulation island : islands)
            pool.submit(() -> {
                evolutionStep.evolve(island);
                counter.countDown();
            });
        counter.await();
        convergence.merge(islands);
    }
}
