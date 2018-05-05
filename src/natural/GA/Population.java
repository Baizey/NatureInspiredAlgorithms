package natural.GA;

import natural.AbstractPopulation;
import natural.interfaces.*;
import natural.interfaces.Crossover;
import natural.interfaces.Mutation;
import natural.interfaces.Selection;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;

@SuppressWarnings({"WeakerAccess", "unused"})
public class Population extends AbstractPopulation {
    private final int popSize, startFrom;
    private final boolean elitism;
    private Individual[] nextGen, currGen;
    private final Selection selection;
    private final GeneticAlgorithmFitness fitness;
    private final Crossover crossover;
    private final Mutation mutator;
    private final PreCalc preCalculations;
    private PreCalcData preCalcData = null;

    public Population(Population other) {
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
        this.currGen = new Individual[popSize];
        this.nextGen = new Individual[popSize];

        int geneSize = other.getBest().getLength();
        for (int i = 0; i < popSize; i++) {
            currGen[i] = new Individual(geneSize, false);
            nextGen[i] = new Individual(geneSize, false);
        }
        for(int i = 0; i < popSize; i++) {
            currGen[i].copyDnaFrom(other.currGen[i]);
            currGen[i].setFitness(other.currGen[i].getFitness());
        }


        if(other.preCalcData != null) {
            long[] longs = other.preCalcData.longs == null ? null
                    : Arrays.copyOf(other.preCalcData.longs, other.preCalcData.longs.length);
            double[] doubles = other.preCalcData.doubles == null ? null
                    : Arrays.copyOf(other.preCalcData.doubles, other.preCalcData.doubles.length);
            preCalcData = new PreCalcData(longs, doubles);
        }
    }

    public void copyPopulationDnaFrom(Population other){
        for(int i = 0; i < popSize; i++)
            currGen[i].copyDnaFrom(other.currGen[i]);
    }

    public Population(
            int popSize, int geneSize,
            boolean elitism,
            boolean generate,
            Mutation mutationInterface,
            GeneticAlgorithmFitness fitnessInterface,
            Crossover crossoverInterface,
            Selection selectionInterface,
            PreCalc preCalculations
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
            preCalculations);
    }

    public Population(
            int popSize, int geneSize,
            boolean elitism,
            boolean generate,
            int maxThreads,
            Mutation mutationInterface,
            GeneticAlgorithmFitness fitnessInterface,
            Crossover crossoverInterface,
            Selection selectionInterface,
            PreCalc preCalculations
    ) {
        super(Math.min(popSize, maxThreads) - (elitism ? 1 : 0), popSize - (elitism ? 1 : 0));
        this.preCalculations = preCalculations;
        this.currGen = new Individual[popSize];
        this.nextGen = new Individual[popSize];
            for (int i = 0; i < popSize; i++) {
                currGen[i] = new Individual(geneSize, generate);
                nextGen[i] = new Individual(geneSize, false);
        }

        this.popSize = popSize;
        this.elitism = elitism;
        this.startFrom = elitism ? 1 : 0;
        this.mutator = mutationInterface;
        this.fitness = fitnessInterface;
        this.crossover = crossoverInterface;
        this.selection = selectionInterface;

        for(Individual individual : currGen)
            fitness.calc(individual);
        findBestFitness();
    }

    @Override
    public void evolve() {
        generation++;
        // Do calculations that may be needed multiple times
        preCalcData = preCalculations.calc(currGen, preCalcData);

        // Move over current best pop if elitism is true
        if (elitism) {
            nextGen[0].setFitness(currGen[0]);
            nextGen[0].copyDnaFrom(currGen[0]);
        }

        for (int i = startFrom; i < popSize; i++) {
            crossover.crossover(preCalcData,
                    selection.select(preCalcData, currGen),
                    selection.select(preCalcData, currGen),
                    nextGen[i]);
            mutator.mutate(preCalcData, nextGen[i]);
            fitness.calc(nextGen[i]);
        }
        Individual[] temp = currGen;
        currGen = nextGen;
        nextGen = temp;
        findBestFitness();
    }


    @Override
    public void evolveParallel() throws InterruptedException {
        generation++;
        preCalcData = preCalculations.calc(currGen, preCalcData);
        if (elitism) {
            nextGen[0].setFitness(currGen[0]);
            nextGen[0].copyDnaFrom(currGen[0]);
        }
        CountDownLatch counter = new CountDownLatch(maxThreads);
        for(int i = startFrom; i < popSize; i += threadWork) {
            int min = i, max = Math.min(popSize, i + threadWork);
            pool.submit(() -> {
                for(int k = min; k < max; k++) {
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
        Individual[] temp = currGen;
        currGen = nextGen;
        nextGen = temp;
        findBestFitness();
    }


    void findBestFitness() {
        // Always ensure the #1 pop is placed at index 0
        int best = 0;
        for (int i = 1; i < popSize; i++)
            if (currGen[i].getFitness() > currGen[best].getFitness())
                best = i;
        if (best != 0) {
            Individual temp = currGen[0];
            currGen[0] = currGen[best];
            currGen[best] = temp;
        }
    }

    public Individual[] getPopulation() {
        return currGen;
    }

    public Individual getBest() {
        return currGen[0];
    }

    public Dna getBestDna() {
        return currGen[0].getDna();
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

    public Individual[] getNextGen() {
        return nextGen;
    }

    public Mutation getMutation() {
        return mutator;
    }

    public GeneticAlgorithmFitness getFitness() {
        return fitness;
    }
}
