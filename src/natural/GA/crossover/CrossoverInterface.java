package natural.GA.crossover;

import natural.GA.Individual;

@SuppressWarnings("unused")
public interface CrossoverInterface {
    void crossover(long[] preCalc, Individual male, Individual female, Individual baby);
}

