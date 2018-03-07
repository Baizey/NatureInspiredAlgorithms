package natural.GA;

import natural.AbstractPopulation;
import natural.GA.crossover.CrossoverInterface;
import natural.GA.fitness.FitnessInterface;
import natural.GA.mutations.MutationInterface;
import natural.GA.preCalc.PreCalcInterface;
import natural.GA.select.SelectionInterface;

@SuppressWarnings({"WeakerAccess", "unused"})
public class Population extends AbstractPopulation {
    private final double mutationRate;
    private final int popSize, startFrom;
    private final boolean elitism;
    private Individual[] nextGen, currGen;
    private final SelectionInterface selection;
    private final FitnessInterface fitness;
    private final CrossoverInterface crossover;
    private final MutationInterface mutator;
    private final PreCalcInterface preCalculations;

    public Population(Population other) {
        this.popSize = other.popSize;
        this.elitism = other.elitism;
        this.generation = other.generation;
        this.mutationRate = other.mutationRate;
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
    }

    public Population(
            int popSize, int geneSize,
            boolean elitism,
            boolean generate,
            double mutationRate,
            MutationInterface mutationInterface,
            FitnessInterface fitnessInterface,
            CrossoverInterface crossoverInterface,
            SelectionInterface selectionInterface,
            PreCalcInterface preCalculations
    ) {
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
        this.mutationRate = mutationRate;
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
        // Fx: sum of fitness
        int[] preCalc = preCalculations.calc(currGen);

        // Move over current best pop if elitism is true
        if (elitism) {
            nextGen[0].setFitness(currGen[0]);
            nextGen[0].copyDnaFrom(currGen[0]);
        }

        // TODO: parallel faster than serial
        /*
        WorkerThread[] workers = new WorkerThread[popSize];
        int working = 0;
        for(int i = startFrom; i < popSize; i += 10, working++)
            (workers[working] = new WorkerThread(this, preCalc, i, Math.min(popSize, i + 10))).start();
        for(int i = 0; i < working; i++)
            workers[i].join();
        */

        for (int i = startFrom; i < popSize; i++) {
            crossover.crossover(preCalc,
                    selection.select(preCalc, currGen),
                    selection.select(preCalc, currGen),
                    nextGen[i]);
            mutator.mutate(preCalc, mutationRate, nextGen[i]);
            fitness.calc(nextGen[i]);
        }
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

    public DNA getBestDna() {
        return currGen[0].getDna();
    }

    @Override
    public int getBestFitness() {
        return currGen[0].getFitness();
    }

    public int getGeneration() {
        return generation;
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

    public double getMutationRate() {
        return mutationRate;
    }
}
