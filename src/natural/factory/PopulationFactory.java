package natural.factory;

import natural.GA.Population;
import natural.GA.crossover.Crossover;
import natural.GA.fitness.Fitness;
import natural.GA.fitness.FitnessInterface;
import natural.GA.mutations.Mutation;
import natural.GA.preCalc.PreCalcs;
import natural.GA.select.Selection;

public class PopulationFactory {

    public static Population oneMax(int geneSize) {
        return new Population(
                2, geneSize, true, false, 1,
                Mutation.flipOne(),
                Fitness.oneMax(),
                Crossover.none(),
                Selection.best(),
                PreCalcs.none()
        );
    }

    public static Population normalPopulation(int geneSize, FitnessInterface fitnessFunction) {
        return new Population(
                100, geneSize, true, true, 0.05,
                Mutation.flipOne(),
                fitnessFunction,
                Crossover.halfAndHalf(),
                Selection.stochasticUniversalSampling(),
                PreCalcs.minAndSum() // Used at Selection
        );
    }

}
