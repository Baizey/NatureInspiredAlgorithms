package natural.genericGA;

import natural.AbstractIndividual;
import natural.AbstractPopulation;
import natural.interfaces.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;

public class GenericPopulation extends AbstractPopulation {
    private final int popSize, startFrom;
    private final boolean elitism;
    private GenericIndividual[] nextGen, currGen;
    private final Selector selector;
    private final Fitness fitness;
    private final Crossover crossover;
    private final Mutator mutator;
    private final int geneSize;

    public GenericPopulation(GenericPopulation other) {
        super(other.maxThreads, other.popSize, other.preCalc);
        this.popSize = other.popSize;
        this.elitism = other.elitism;
        this.generation = other.generation;
        this.startFrom = other.startFrom;
        this.selector = other.selector;
        this.fitness = other.fitness;
        this.crossover = other.crossover;
        this.mutator = other.mutator;
        this.currGen = new GenericIndividual[popSize];
        this.nextGen = new GenericIndividual[popSize];

        geneSize = other.getBest().getLength();
        for (int i = 0; i < popSize; i++) {
            currGen[i] = other.currGen[i].clone(false);
            nextGen[i] = other.nextGen[i].clone(false);
        }

        this.memory = new HashMap<>();
        this.memory.putAll(other.memory);
    }

    @Override
    public void copyPopulation(AbstractPopulation other) {
        GenericPopulation pops = (GenericPopulation) other;
        for (int i = 0; i < popSize; i++)
            currGen[i].copy(pops.currGen[i]);
    }

    public GenericPopulation(
            int popSize, int geneSize,
            boolean elitism,
            boolean generate,
            Mutator mutationInterface,
            Fitness fitnessInterface,
            Crossover crossoverInterface,
            Selector selectorInterface,
            PreCalc preCalculations,
            GenericIndividual prototype
    ) {
        this(popSize,
                geneSize,
                elitism,
                generate,
                Runtime.getRuntime().availableProcessors(),
                mutationInterface,
                fitnessInterface,
                crossoverInterface,
                selectorInterface,
                preCalculations,
                prototype);
    }

    public GenericPopulation(
            int popSize, int geneSize,
            boolean elitism,
            boolean generate,
            int maxThreads,
            Mutator mutationInterface,
            Fitness fitnessInterface,
            Crossover crossoverInterface,
            Selector selectorInterface,
            PreCalc preCalc,
            GenericIndividual individualPrototype
    ) {
        super(Math.min(popSize, maxThreads) - (elitism ? 1 : 0), popSize - (elitism ? 1 : 0), preCalc);
        this.geneSize = geneSize;
        this.currGen = new GenericIndividual[popSize];
        this.nextGen = new GenericIndividual[popSize];
        for (int i = 0; i < popSize; i++) {
            currGen[i] = individualPrototype.clone(generate);
            nextGen[i] = individualPrototype.clone(false);
        }

        this.popSize = popSize;
        this.elitism = elitism;
        this.startFrom = elitism ? 1 : 0;
        this.mutator = mutationInterface;
        this.fitness = fitnessInterface;
        this.crossover = crossoverInterface;
        this.selector = selectorInterface;

        this.memory = preCalc.calc(currGen, this.memory);
        for (GenericIndividual individual : currGen)
            fitness.calc(memory, individual);
        findBestFitness();
    }

    @Override
    public void evolve() {
        generation++;
        // Do calculations that may be needed multiple times
        memory = preCalc.calc(currGen, memory);

        // Move over current best pop if elitism is true
        if (elitism)
            nextGen[0].copy(currGen[0]);

        for (int i = startFrom; i < popSize; i++) {
            crossover.crossover(memory,
                    selector.select(memory, currGen),
                    selector.select(memory, currGen),
                    nextGen[i]);
            mutator.mutate(memory, nextGen[i]);
            fitness.calc(memory, nextGen[i]);
        }

        var temp = currGen;
        currGen = nextGen;
        nextGen = temp;
        findBestFitness();
    }


    @Override
    public void evolveParallel() throws InterruptedException {
        generation++;
        memory = preCalc.calc(currGen, memory);
        if (elitism)
            nextGen[0].copy(currGen[0]);
        var counter = new CountDownLatch(maxThreads);
        for (int i = startFrom; i < popSize; i += threadWork) {
            int min = i,
                max = Math.min(popSize, i + threadWork);
            pool.submit(() -> {
                for (int k = min; k < max; k++) {
                    crossover.crossover(memory,
                            selector.select(memory, currGen),
                            selector.select(memory, currGen),
                            nextGen[k]);
                    mutator.mutate(memory, nextGen[k]);
                    fitness.calc(memory, nextGen[k]);
                }
                counter.countDown();
            });
        }
        counter.await();
        var temp = currGen;
        currGen = nextGen;
        nextGen = temp;
        findBestFitness();
    }


    private void findBestFitness() {
        // Always ensure the #1 pop is placed at index 0
        int best = 0;
        for (int i = 1; i < popSize; i++)
            if (currGen[i].getFitness() > currGen[best].getFitness())
                best = i;
        if (best != 0) {
            GenericIndividual temp = currGen[0];
            currGen[0] = currGen[best];
            currGen[best] = temp;
        }
    }

    @Override
    public AbstractIndividual[] getPopulation() {
        return currGen;
    }

    @Override
    public AbstractIndividual getIndividual(int index) { return currGen[index]; }

    public AbstractIndividual getBest() {
        return currGen[0];
    }

    public Object getBestDna() {
        return currGen[0].getSolution();
    }

    @Override
    public long getBestFitness() {
        return currGen[0].getFitness();
    }

    @Override
    public double getMeanFitness() {
        return Arrays.stream(currGen)
                .mapToLong(AbstractIndividual::getFitness)
                .average().orElse(AbstractIndividual.UNSET_FITNESS);
    }

    public Selector getSelectionInterface() {
        return selector;
    }

    public Crossover getCrossover() {
        return crossover;
    }

    public GenericIndividual[] getPreviousGeneration() {
        return nextGen;
    }

    public Mutator getMutation() {
        return mutator;
    }

    public Fitness getFitness() {
        return fitness;
    }

    public int getGeneSize() {
        return geneSize;
    }
}
