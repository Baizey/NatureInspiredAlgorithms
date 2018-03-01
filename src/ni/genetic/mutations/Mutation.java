package ni.genetic.mutations;

import java.util.Random;

@SuppressWarnings("unused")
public class Mutation {

    /**
     * No mutation
     *
     * @return individual mutated
     */
    public static MutationInterface flipNone() {
        return (preCalc, individual, mutationRate) -> {};
    }

    /**
     * If mutating flip 1 random gene
     *
     * @return individual mutated
     */
    public static MutationInterface flipOne() {
        return (preCalc, individual, mutationRate) -> {
            if (Math.random() < mutationRate) {
                int i = new Random().nextInt(individual.getLength());
                individual.getDna().flip(i);
                individual.resetFitness();
            }
        };
    }

    /**
     * For each gene check if it should be flipped
     *
     * @return individual mutated
     */
    public static MutationInterface flipRandom() {
        return (preCalc, individual, mutationRate) -> {
            for (int i = 0; i < individual.getLength(); i++)
                if (Math.random() < mutationRate)
                    individual.getDna().flip(i);
            individual.resetFitness();
        };
    }

}

