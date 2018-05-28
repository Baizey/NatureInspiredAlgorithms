package natural.genericGA;

import natural.AbstractPopulation;
import natural.genericGA.binaryGA.PreCalcData;
import natural.interfaces.*;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;

public class GenericPopulation<T> extends AbstractPopulation {
    private final int popSize, startFrom;
    private final boolean elitism;
    private GenericIndividual[] nextGen, currGen;
    private final Selection selection;
    private final Fitness fitness;
    private final Crossover crossover;
    private final Mutation mutator;
    private final PreCalc preCalculations;
    private final int geneSize;
    private PreCalcData preCalcData = null;

    public GenericPopulation(GenericPopulation<T> other) {
        super(other.maxThreads, other.popSize);
        this.popSize = other.popSize;
        this.elitism = other.elitism;
        this.generation = other.generation;
        this.startFrom = other.startFrom;
        this.selection = other.selection;
        this.fitness = other.fitness;
        this.crossover = other.crossover;
        this.mutator = other.mutator;
        this.preCalculations = other.preCalculations;
        this.currGen = new GenericIndividual[popSize];
        this.nextGen = new GenericIndividual[popSize];

        geneSize = other.getBest().getLength();
        for (int i = 0; i < popSize; i++) {
            currGen[i] = other.currGen[i].clone(false);
            nextGen[i] = other.nextGen[i].clone(false);
        }

        if (other.preCalcData != null) {
            long[] longs = other.preCalcData.longs == null ? null
                    : Arrays.copyOf(other.preCalcData.longs, other.preCalcData.longs.length);
            double[] doubles = other.preCalcData.doubles == null ? null
                    : Arrays.copyOf(other.preCalcData.doubles, other.preCalcData.doubles.length);
            String[] strings = other.preCalcData.strings == null ? null
                    : Arrays.copyOf(other.preCalcData.strings, other.preCalcData.strings.length);
            preCalcData = new PreCalcData(longs, doubles, strings);
        }
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
            Mutation mutationInterface,
            Fitness fitnessInterface,
            Crossover crossoverInterface,
            Selection selectionInterface,
            PreCalc preCalculations,
            GenericIndividual<T> prototype
    ) {
        this(popSize,
                geneSize,
                elitism,
                generate,
                Runtime.getRuntime().availableProcessors(),
                mutationInterface,
                fitnessInterface,
                crossoverInterface,
                selectionInterface,
                preCalculations,
                prototype);
    }

    public GenericPopulation(
            int popSize, int geneSize,
            boolean elitism,
            boolean generate,
            int maxThreads,
            Mutation mutationInterface,
            Fitness fitnessInterface,
            Crossover crossoverInterface,
            Selection selectionInterface,
            PreCalc preCalculations,
            GenericIndividual<T> individualPrototype
    ) {
        super(Math.min(popSize, maxThreads) - (elitism ? 1 : 0), popSize - (elitism ? 1 : 0));
        this.geneSize = geneSize;
        this.preCalculations = preCalculations;
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
        this.selection = selectionInterface;

        for (GenericIndividual individual : currGen)
            fitness.calc(individual);
        findBestFitness();
    }

    @Override
    public void evolve() {
        generation++;
        // Do calculations that may be needed multiple times
        preCalcData = preCalculations.calc(currGen, preCalcData);

        // Move over current best pop if elitism is true
        if (elitism)
            nextGen[0].copy(currGen[0]);

        for (int i = startFrom; i < popSize; i++) {
            crossover.crossover(preCalcData,
                    selection.select(preCalcData, currGen),
                    selection.select(preCalcData, currGen),
                    nextGen[i]);
            mutator.mutate(preCalcData, nextGen[i]);
            fitness.calc(nextGen[i]);
        }

        var temp = currGen;
        currGen = nextGen;
        nextGen = temp;
        findBestFitness();
    }


    @Override
    public void evolveParallel() throws InterruptedException {
        generation++;
        preCalcData = preCalculations.calc(currGen, preCalcData);
        if (elitism)
            nextGen[0].copy(currGen[0]);
        var counter = new CountDownLatch(maxThreads);
        for (int i = startFrom; i < popSize; i += threadWork) {
            int min = i,
                max = Math.min(popSize, i + threadWork);
            pool.submit(() -> {
                for (int k = min; k < max; k++) {
                    crossover.crossover(preCalcData,
                            selection.select(preCalcData, currGen),
                            selection.select(preCalcData, currGen),
                            nextGen[k]);
                    mutator.mutate(preCalcData, nextGen[k]);
                    fitness.calc(nextGen[k]);
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

    public GenericIndividual[] getPopulation() {
        return currGen;
    }

    public GenericIndividual getBest() {
        return currGen[0];
    }

    public T getBestDna() {
        return (T) currGen[0].getDna();
    }

    @Override
    public long getBestFitness() {
        return currGen[0].getFitness();
    }

    public Selection getSelectionInterface() {
        return selection;
    }

    public Crossover getCrossover() {
        return crossover;
    }

    public GenericIndividual[] getNextGen() {
        return nextGen;
    }

    public Mutation getMutation() {
        return mutator;
    }

    public Fitness getFitness() {
        return fitness;
    }

    public int getGeneSize() {
        return geneSize;
    }
}
