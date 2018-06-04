package natural.genericGA.binaryGA;

import natural.genericGA.GenericPopulation;
import natural.interfaces.Crossover;
import natural.interfaces.*;
import natural.interfaces.Fitness;
import natural.interfaces.Mutator;
import natural.interfaces.Selector;

@SuppressWarnings({"WeakerAccess", "unused"})
public class BinaryPopulation extends GenericPopulation {

    public BinaryPopulation(GenericPopulation other) {
        super(other);
    }

    public BinaryPopulation(int popSize, int geneSize,
                            boolean elitism, boolean generate,
                            Mutator mutation,
                            Fitness fitness,
                            Crossover crossover,
                            Selector selector,
                            PreCalc pre) {
        super(popSize, geneSize,
                elitism, generate,
                mutation,
                fitness,
                crossover,
                selector,
                pre,
                new BinaryIndividual(geneSize, generate));
    }

    public BinaryPopulation(int popSize, int geneSize,
                            boolean elitism, boolean generate,
                            int maxThreads,
                            Mutator mutationInterface,
                            Fitness fitness,
                            Crossover crossoverInterface,
                            Selector selectorInterface,
                            PreCalc preCalculations) {
        super(popSize, geneSize, elitism, generate, maxThreads, mutationInterface, fitness, crossoverInterface, selectorInterface, preCalculations, new BinaryIndividual(geneSize, generate));
    }
}
