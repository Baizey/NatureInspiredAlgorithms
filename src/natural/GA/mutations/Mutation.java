package natural.GA.mutations;

import java.util.Random;

@SuppressWarnings("unused")
public class Mutation {
    private static final Random random = new Random();
    /**
     * No mutation
     *
     * @return individual mutated
     */
    public static MutationInterface flipNone() {
        return (preCalc, individual, mutationRate) -> {
        };
    }

    /**
     * If mutating flip 1 random gene
     *
     * @return individual mutated
     */
    public static MutationInterface flipOne() {
        return (preCalc, mutationRate, individual) -> {
            if (Math.random() < mutationRate) {
                int i = random.nextInt(individual.getLength());
                individual.getDna().flip(i);
                individual.resetFitness();
            }
        };
    }

    public static MutationInterface flipX(int x) {
        return (preCalc, mutationRate, individual) -> {
            for (int i = 0; i < x; i++) {
                if (Math.random() < mutationRate) {
                    int index = random.nextInt(individual.getLength());
                    individual.getDna().flip(index);
                    individual.resetFitness();
                }
            }
        };
    }

    /**
     * For each gene check if it should be flipped
     *
     * @return individual mutated
     */
    public static MutationInterface flipRandom() {
        return (preCalc, mutationRate, individual) -> {
            for (int i = 0; i < individual.getLength(); i++)
                if (Math.random() < mutationRate)
                    individual.getDna().flip(i);
            individual.resetFitness();
        };
    }

}

