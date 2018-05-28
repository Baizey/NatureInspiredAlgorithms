package natural.genericGA.binaryGA;

import natural.genericGA.GenericPopulation;
import natural.interfaces.Crossover;
import natural.interfaces.*;
import natural.interfaces.Fitness;
import natural.interfaces.Mutation;
import natural.interfaces.Selection;

@SuppressWarnings({"WeakerAccess", "unused"})
public class BinaryPopulation extends GenericPopulation<Dna> {

    public BinaryPopulation(GenericPopulation<Dna> other) {
        super(other);
    }

    public BinaryPopulation(int popSize, int geneSize,
                            boolean elitism, boolean generate,
                            Mutation mutation,
                            Fitness fitness,
                            Crossover crossover,
                            Selection selection,
                            PreCalc pre) {
        super(popSize, geneSize,
                elitism, generate,
                mutation,
                fitness,
                crossover,
                selection,
                pre,
                new BinaryIndividual(geneSize, generate));
    }

    public BinaryPopulation(int popSize, int geneSize,
                            boolean elitism, boolean generate,
                            int maxThreads,
                            Mutation mutationInterface,
                            Fitness fitness,
                            Crossover crossoverInterface,
                            Selection selectionInterface,
                            PreCalc preCalculations) {
        super(popSize, geneSize, elitism, generate, maxThreads, mutationInterface, fitness, crossoverInterface, selectionInterface, preCalculations, new BinaryIndividual(geneSize, generate));
    }
}
