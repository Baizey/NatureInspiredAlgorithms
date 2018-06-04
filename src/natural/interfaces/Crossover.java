package natural.interfaces;

import natural.genericGA.GenericIndividual;

import java.util.HashMap;

@SuppressWarnings("unused")
public interface Crossover {
    void crossover(HashMap<String, Object> memory, GenericIndividual male, GenericIndividual female, GenericIndividual baby);
}

