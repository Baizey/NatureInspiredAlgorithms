package ni.genetic.breed;

import ni.genetic.Individual;

@SuppressWarnings("unused")
public interface BreedInterface {
    void breed(int[] preCalc, Individual male, Individual female, Individual baby);
}

