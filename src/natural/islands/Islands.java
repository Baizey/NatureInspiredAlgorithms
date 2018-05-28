package natural.islands;

import lsm.helpers.IO.write.text.console.Note;
import natural.AbstractIndividual;
import natural.AbstractPopulation;
import natural.interfaces.Convergence;
import natural.interfaces.EvolutionStep;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * evolve and evolveParallel are the same for Islands
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class Islands extends AbstractPopulation {

    private final AbstractPopulation[] islands;
    private final Convergence convergence;
    private final EvolutionStep evolutionStep;

    public Islands(
            Convergence convergence,
            EvolutionStep evolutionStep,
            AbstractPopulation... populations
    ) {
        super(populations.length, populations.length);
        this.islands = populations;
        this.convergence = convergence;
        this.evolutionStep = evolutionStep;
    }

    @Override
    public void evolve() throws Exception { evolveParallel(); }

    @Override
    public void copyPopulation(AbstractPopulation other) {
        Islands islands = (Islands) other;
        for (int i = 0; i < this.islands.length; i++)
            this.islands[i].copyPopulation((islands).islands[i]);
    }

    @Override
    public void evolveParallel() throws Exception {
        CountDownLatch counter = new CountDownLatch(islands.length);
        for (AbstractPopulation island : islands)
            pool.submit(() -> {
                evolutionStep.evolve(island);
                counter.countDown();
                return null;
            });
        Note.writenl("Waiting");
        counter.await(100, TimeUnit.MINUTES);
        Note.writenl("Done");
        convergence.merge(islands);
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
        return getBestIsland().getBest();
    }

    @Override
    public long getBestFitness() {
        return getBest().getFitness();
    }
}
