package natural.genericGA.binaryGA;

import natural.interfaces.Selector;

import java.util.Random;

public class Selection {

    private static final Random random = new Random();

    public static Selector random() {
        return (preCalc, individuals) -> individuals[(int) (Math.random() * individuals.length)];
    }

    public static Selector stochasticUniversalSampling() {
        return (preCalc, individuals) -> {
            long min = Math.abs((Long) preCalc.getOrDefault("min", 0L));
            long sum = (Long) preCalc.getOrDefault("sum", 0L) + 1L;
            double pick = Math.abs(random.nextLong()) % sum;
            for (var individual : individuals) {
                pick -= individual.getFitness() + min;
                if (pick <= 0) return individual;
            }
            return null; // If this happens an error occurred
        };
    }

    public static Selector best() {
        return (preCalc, individuals) -> {
            var best = individuals[0];
            for(var i = 1; i < individuals.length; i++)
                if(individuals[i].getFitness() > best.getFitness())
                    best = individuals[i];
            return best;
        };
    }

    public static Selector get(String selectionChoice) {
        switch (selectionChoice.toLowerCase()) {
            case "stochastic":
                return stochasticUniversalSampling();
            case "random":
                return random();
            default:
                return best();
        }
    }
}
