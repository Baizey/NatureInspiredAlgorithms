package natural.factory;

import natural.GA.Population;
import natural.GA.Crossover;
import natural.GA.Fitness;
import natural.interfaces.GeneticAlgorithmFitness;
import natural.GA.Mutation;
import natural.GA.PreCalcs;
import natural.GA.Selection;

public class PopulationFactory {


    public static Population leadingOnes(int geneSize) {
        return leadingOnes(geneSize, false);
    }
    public static Population leadingOnes(int geneSize, boolean randomBias) {
        return new Population(
                2, geneSize, true, randomBias,
                Mutation.flipRandomExact(),
                Fitness.leadingOnes(),
                Crossover.none(),
                Selection.best(),
                PreCalcs.exactPrePreCalculatedSkipChance()
        );
    }

    public static Population oneMax(int geneSize) {
        return oneMax(geneSize, false);
    }
    public static Population oneMax(int geneSize, boolean randomBias) {
        return new Population(
                2, geneSize, true, randomBias,
                Mutation.flipRandomExact(),
                Fitness.oneMax(),
                Crossover.none(),
                Selection.best(),
                PreCalcs.exactPrePreCalculatedSkipChance()
        );
    }

    public static Population normalPopulation(int geneSize, GeneticAlgorithmFitness fitnessFunction) {
        return new Population(
                100, geneSize, true, true,
                Mutation.flipOne(0.05D),
                fitnessFunction,
                Crossover.halfAndHalf(),
                Selection.stochasticUniversalSampling(),
                PreCalcs.minAndSum() // Used at Selection
        );
    }

}
