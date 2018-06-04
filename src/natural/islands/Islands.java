package natural.islands;

import natural.AbstractIndividual;
import natural.AbstractPopulation;
import natural.PreCalcs;
import natural.interfaces.Convergence;
import natural.interfaces.EvolutionStep;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CountDownLatch;
import java.util.function.Function;

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
        super(populations.length, populations.length, PreCalcs.none());
        this.islands = populations;
        this.convergence = convergence;
        this.evolutionStep = evolutionStep;
    }

    @Override
    public void copyPopulation(AbstractPopulation other) {
        Islands islands = (Islands) other;
        for (int i = 0; i < this.islands.length; i++)
            this.islands[i].copyPopulation(islands.islands[i]);
    }

    @Override
    public AbstractIndividual[] getPopulation() {
        return Arrays.stream(islands).map((Function<AbstractPopulation, Object>) AbstractPopulation::getPopulation)
                .map(Arrays::asList).flatMap(Collection::stream).toArray(AbstractIndividual[]::new);
    }

    @Override
    public AbstractIndividual getIndividual(int index) throws ArrayIndexOutOfBoundsException {
        if(index < 0)
            throw new ArrayIndexOutOfBoundsException("");
        for(AbstractPopulation island : islands) {
            if(index < island.getPopulationSize())
                return island.getIndividual(index);
            index -= island.getPopulationSize();
        }
        throw new ArrayIndexOutOfBoundsException("");
    }

    @Override
    public void evolve() throws Exception {
        for (AbstractPopulation island : islands)
            evolutionStep.evolve(island);
        convergence.converge(islands);
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
        counter.await();
        convergence.converge(islands);
    }

    @Override
    public AbstractIndividual getBest() {
        int best = 0;
        for (int i = 1; i < islands.length; i++)
            if (islands[i].getBestFitness() > islands[best].getBestFitness())
                best = i;
        return islands[best].getBest();
    }

    @Override
    public long getBestFitness() {
        return getBest().getFitness();
    }

    @Override
    public double getMeanFitness() {
        return Arrays.stream(islands).mapToDouble(AbstractPopulation::getMeanFitness).average().orElse(AbstractIndividual.UNSET_FITNESS);
    }

    public AbstractPopulation getIsland(int i) {
        return islands[i];
    }
}
