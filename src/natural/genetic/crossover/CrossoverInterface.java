package natural.genetic.crossover;

import natural.genetic.Individual;

@SuppressWarnings("unused")
public interface CrossoverInterface {
    void breed(double[] preCalc, Individual male, Individual female, Individual baby);
}

