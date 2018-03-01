package ni.genetic.crossover;

import ni.genetic.Individual;

@SuppressWarnings("unused")
public interface CrossoverInterface {
    void breed(int[] preCalc, Individual male, Individual female, Individual baby);
}

