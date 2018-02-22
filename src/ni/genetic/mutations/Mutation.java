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
                Boolean[] genes = individual.genes;
                int i = new Random().nextInt(individual.geneSize);
                genes[i] = !genes[i];
                individual.reset();
            }
        };
    }

    /**
     * If mutating flip ALL genes
     *
     * @return individual mutated
     */
    public static MutationInterface flipAll() {
        return (preCalc, individual, mutationRate) -> {
            if (Math.random() < mutationRate) {
                Boolean[] genes = individual.genes;
                for(int i = 0; i < genes.length; i++) genes[i] = !genes[i];
                individual.reset();
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
            Boolean[] genes = individual.genes;
            for (int i = 0; i < individual.geneSize; i++)
                if (Math.random() < mutationRate) genes[i] = !genes[i];
            individual.reset();
        };
    }

}

