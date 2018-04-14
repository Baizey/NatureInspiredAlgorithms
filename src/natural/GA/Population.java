package natural.GA;

import lsm.helpers.IO.write.text.console.Note;
import natural.AbstractPopulation;
import natural.GA.crossover.CrossoverInterface;
import natural.GA.fitness.FitnessInterface;
import natural.GA.mutations.MutationInterface;
import natural.GA.preCalc.PreCalcData;
import natural.GA.preCalc.PreCalcInterface;
import natural.GA.select.SelectionInterface;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@SuppressWarnings({"WeakerAccess", "unused"})
public class Population extends AbstractPopulation {
    private final int popSize, startFrom;
    private final boolean elitism;
    private Individual[] nextGen, currGen;
    private final SelectionInterface selection;
    private final FitnessInterface fitness;
    private final CrossoverInterface crossover;
    private final MutationInterface mutator;
    private final PreCalcInterface preCalculations;
    private PreCalcData preCalcData = null;

    public Population(Population other) {
        super(other.pool != null);
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

        long[] longs = other.preCalcData.longs == null ? null
                : Arrays.copyOf(other.preCalcData.longs, other.preCalcData.longs.length);
        double[] doubles = other.preCalcData.doubles == null ? null
                : Arrays.copyOf(other.preCalcData.doubles, other.preCalcData.doubles.length);
        preCalcData = new PreCalcData(longs, doubles);
    }

    public void copyPopulationDnaFrom(Population other){
        for(int i = 0; i < popSize; i++)
            currGen[i].copyDnaFrom(other.currGen[i]);
    }

    public Population(
            int popSize, int geneSize,
            boolean elitism,
            boolean generate,
            boolean useParallel,
            MutationInterface mutationInterface,
            FitnessInterface fitnessInterface,
            CrossoverInterface crossoverInterface,
            SelectionInterface selectionInterface,
            PreCalcInterface preCalculations
    ) {
        super(useParallel);
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
        // Do calculations that may be needed multiple times
        preCalcData = preCalculations.calc(currGen, preCalcData);

        // Move over current best pop if elitism is true
        if (elitism) {
            nextGen[0].setFitness(currGen[0]);
            nextGen[0].copyDnaFrom(currGen[0]);
        }

        for (int i = startFrom; i < popSize; i += 999) {
            int min = i;
            int max = Math.min(min + 999, popSize);
            pool.submit(() -> {
                for(int k = min; k < max; k++) {
                    crossover.crossover(preCalcData,
                            selection.select(preCalcData, currGen),
                            selection.select(preCalcData, currGen),
                            nextGen[k]);
                    mutator.mutate(preCalcData, nextGen[k]);
                    fitness.calc(nextGen[k]);
                }
            });
        }
        pool.awaitTermination(1, TimeUnit.MILLISECONDS);
        Individual[] temp = currGen;
        currGen = nextGen;
        nextGen = temp;
        findBestFitness();
        Note.writenl(getBest());
        Note.writenl(getBestFitness());
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

    public SelectionInterface getSelectionInterface() {
        return selection;
    }

    public CrossoverInterface getCrossover() {
        return crossover;
    }

    public Individual[] getNextGen() {
        return nextGen;
    }

    public MutationInterface getMutation() {
        return mutator;
    }

    public FitnessInterface getFitness() {
        return fitness;
    }
}
