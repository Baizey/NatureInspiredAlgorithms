package natural.GA.select;

import natural.GA.Individual;

import java.util.Random;

public class Selection {

    private static final Random random = new Random();

    public static SelectionInterface random(){
        return (preCalc, individuals) -> individuals[(int) (Math.random() * individuals.length)];
    }

    public static SelectionInterface stochasticUniversalSampling(){
        return (preCalc, individuals) -> {
            long min = Math.abs(preCalc.longs[0]);
            long sum = preCalc.longs[1] + 1;
            double pick = Math.abs(random.nextLong()) % sum;
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

    public static SelectionInterface get(String selectionChoice) {
        switch(selectionChoice.toLowerCase()) {
            case "stochastic": return stochasticUniversalSampling();
            case "random": return random();
            default: return best();
        }
    }
}
