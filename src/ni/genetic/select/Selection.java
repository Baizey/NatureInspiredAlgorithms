package ni.genetic.select;

import ni.genetic.Individual;

import java.util.Random;

public class Selection {

    public static SelectionInterface random(){
        return (preCalc, individuals) -> individuals[(int) (Math.random() * individuals.length)];
    }

    public static SelectionInterface stochasticUniversalSampling(){
        return (preCalc, individuals) -> {
            int min = Math.abs(preCalc[0]);
            int sum = preCalc[1] + 1;
            int pick = new Random().nextInt(sum);
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
