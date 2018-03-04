package natural.genetic.fitness;

import natural.genetic.Individual;

@SuppressWarnings("unused")
public interface FitnessInterface {
    int calc(double[] preCalc, Individual individual);
}
