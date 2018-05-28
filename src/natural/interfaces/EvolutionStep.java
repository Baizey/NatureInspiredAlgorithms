package natural.interfaces;

import natural.AbstractPopulation;

public interface EvolutionStep {
    void evolve(AbstractPopulation island) throws Exception;
}
