package ni.genetic;

import ni.genetic.breed.Breeding;
import ni.genetic.fitness.Fitness;
import ni.genetic.mutations.Mutation;
import ni.genetic.preCalc.PreCalcs;
import ni.genetic.select.Selection;

public class Driver {

    public static void main(String... args) {

        double mutationRate = 1;

        for (int popSize = 10; popSize <= 100000; popSize *= 2) {

            Population population = new Population(
                    popSize, 100, 1, false, mutationRate,
                    Mutation.flipOne(),
                    Fitness.oneMax(),
                    Breeding.halfAndHalf(),
                    Selection.stochasticUniversalSampling(),
                    PreCalcs.none()
            );
            long start = System.currentTimeMillis();
            population.evolve(popSize);
            long end = System.currentTimeMillis() - start;
            System.out.println(popSize + ": " + (end));
        }
    }

}
