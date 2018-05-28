package natural.factory;

import natural.genericGA.binaryGA.*;

import java.util.Random;

public class PopulationFactory {

    private static final Random random = new Random();

    public static BinaryPopulation leadingOnes(int geneSize) {
        return leadingOnes(geneSize, false);
    }

    public static BinaryPopulation leadingOnes(int geneSize, boolean randomBias) {
        return new BinaryPopulation(
                2, geneSize, true, randomBias,
                Mutation.onePlusOne(),
                Fitness.leadingOnes(),
                Crossover.none(),
                Selection.best(),
                PreCalcs.none()
        );
    }

    public static BinaryPopulation oneMax(int geneSize) {
        return oneMax(geneSize, false);
    }

    public static BinaryPopulation oneMax(int geneSize, boolean randomBias) {
        return new BinaryPopulation(
                2, geneSize, true, randomBias,
                Mutation.onePlusOne(),
                Fitness.oneMax(),
                Crossover.none(),
                Selection.best(),
                PreCalcs.none()
        );
    }

    public static BinaryPopulation normalPopulation(int geneSize, natural.interfaces.Fitness fitnessFunction) {
        return new BinaryPopulation(
                100, geneSize, true, true,
                Mutation.flipOne(0.05D),
                fitnessFunction,
                Crossover.halfAndHalf(),
                Selection.stochasticUniversalSampling(),
                PreCalcs.minAndSum() // Used at Selection
        );
    }

}
