package natural.genetic.select;

import natural.genetic.Individual;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Selection {

    private static final Random random = ThreadLocalRandom.current();

    public static SelectionInterface random(){
        return (preCalc, individuals) -> individuals[(int) (Math.random() * individuals.length)];
    }

    public static SelectionInterface stochasticUniversalSampling(){
        return (preCalc, individuals) -> {
            double min = Math.abs(preCalc[0]);
            double sum = preCalc[1] + 1;
            double pick = random.nextDouble() * sum - 0.0000002;
            for (Individual individual : individuals) {
                pick -= individual.getFitness() + min;
                if (pick <= 0) return individual;
            }
            return null; // If this happens an error occurred
        };
    }

    public static SelectionInterface best() {
        return (preCalc, individuals) -> individuals[0];
    }
}
