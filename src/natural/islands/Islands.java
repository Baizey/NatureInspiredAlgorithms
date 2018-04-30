package natural.islands;

import natural.AbstractIndividual;
import natural.AbstractPopulation;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@SuppressWarnings({"unused", "WeakerAccess"})
public class Islands extends AbstractPopulation {

    private final AbstractPopulation[] islands;
    private final ConvergenceInterface convergence;
    private final EvolutionStepInterface evolutionStep;

    public Islands(
            ConvergenceInterface convergence,
            EvolutionStepInterface evolutionStep,
            AbstractPopulation... populations
    ) {
        super(populations.length, populations.length);
        this.islands = populations;
        this.convergence = convergence;
        this.evolutionStep = evolutionStep;
    }

    @Override
    public void evolve() { evolveParallel(); }

    @Override
    public void evolveParallel() {
        CountDownLatch counter = new CountDownLatch(islands.length);
        for (AbstractPopulation island : islands)
            pool.submit(() -> {
                evolutionStep.evolve(island);
                counter.countDown();
                return null;
            });
        try { counter.await(1000, TimeUnit.SECONDS); } catch (InterruptedException ignored) {}
        convergence.merge(islands);
    }

    @Override
    public long getBestFitness() {
        return 0;
    }

    public AbstractPopulation getIsland(int i) {
        return islands[i];
    }


    public AbstractPopulation getBestIsland() {
        int best = 0;
        for(int i = 1; i < islands.length; i++)
            if(islands[i].getBestFitness() > islands[best].getBestFitness())
                best = i;
        return islands[best];
    }

    @Override
    public AbstractIndividual getBest() {
        int best = 0;
        for(int i = 1; i < islands.length; i++)
            if(islands[i].getBestFitness() > islands[best].getBestFitness())
                best = i;
        return islands[best].getBest();
    }
}
