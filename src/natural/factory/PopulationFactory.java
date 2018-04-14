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
        if (geneSize >= 1000) {
            return new Population(
                    2, geneSize, true, false, true,
                    Mutation.flipRandomExact(),
                    Fitness.oneMax(),
                    Crossover.none(),
                    Selection.best(),
                    PreCalcs.exactPrePreCalculatedSkipChance()
            );
        } else {
            return new Population(
                    2, geneSize, true, false, true,
                    Mutation.flipRandomCheap(1D / geneSize),
                    Fitness.oneMax(),
                    Crossover.none(),
                    Selection.best(),
                    PreCalcs.cheapSkipChance(1D / geneSize)
            );
        }
    }

    public static Population normalPopulation(int geneSize, FitnessInterface fitnessFunction) {
        return new Population(
                100, geneSize, true, true, true,
                Mutation.flipOne(0.05D),
                fitnessFunction,
                Crossover.halfAndHalf(),
                Selection.stochasticUniversalSampling(),
                PreCalcs.minAndSum() // Used at Selection
        );
    }

}
