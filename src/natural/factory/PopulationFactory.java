package natural.factory;

import natural.FitnessFunctions;
import natural.PreCalcs;
import natural.genericGA.binaryGA.*;

import java.util.Arrays;
import java.util.Random;
import java.util.stream.IntStream;

public class PopulationFactory {

    private static final Random random = new Random();

    public static BinaryPopulation leadingOnes(int geneSize) {
        return leadingOnes(geneSize, false);
    }

    public static BinaryPopulation leadingOnes(int geneSize, boolean randomBias) {
        return new BinaryPopulation(
                2, geneSize, true, randomBias,
                BinaryMutation.onePlusOne(),
                FitnessFunctions.leadingOnes(),
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
                BinaryMutation.onePlusOne(),
                FitnessFunctions.oneMax(),
                Crossover.none(),
                Selection.best(),
                PreCalcs.none()
        );
    }

    public static BinaryPopulation subsetSum(int geneSize, int goal) {
        return subsetSum(geneSize, goal, false);
    }

    public static BinaryPopulation subsetSum(int geneSize, int goal, boolean randomBias) {
        int[] nums = IntStream.range(1, geneSize + 1).toArray();
        int sum = Arrays.stream(nums).sum();
        return new BinaryPopulation(
                2, geneSize, true, randomBias,
                BinaryMutation.onePlusOne(),
                FitnessFunctions.subsetSum(sum, nums),
                Crossover.none(),
                Selection.best(),
                PreCalcs.none()
        );
    }

    public static BinaryPopulation normalPopulation(int geneSize, natural.interfaces.Fitness fitnessFunction) {
        return new BinaryPopulation(
                100, geneSize, true, true,
                BinaryMutation.flipOne(0.05D),
                fitnessFunction,
                Crossover.halfAndHalf(),
                Selection.stochasticUniversalSampling(),
                PreCalcs.minAndSum() // Used at Selector
        );
    }

}
