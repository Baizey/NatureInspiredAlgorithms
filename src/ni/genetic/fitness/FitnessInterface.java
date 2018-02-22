package ni.genetic.fitness;

import ni.genetic.Individual;

@SuppressWarnings("unused")
public interface FitnessInterface {
    int calc(int[] preCalc, Individual individual);
}
