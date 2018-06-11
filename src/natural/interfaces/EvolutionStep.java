package natural.interfaces;

import natural.AbstractPopulation;

import java.util.HashMap;

public interface EvolutionStep {
    void evolve(HashMap<String, Object> memory, AbstractPopulation island) throws Exception;
}
