package natural.GA.crossover;

import natural.GA.Individual;

@SuppressWarnings("unused")
public interface CrossoverInterface {
    void crossover(int[] preCalc, Individual male, Individual female, Individual baby);
}

