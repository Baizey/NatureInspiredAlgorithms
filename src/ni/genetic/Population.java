package ni.genetic;

import ni.genetic.breed.BreedInterface;
import ni.genetic.fitness.FitnessInterface;
import ni.genetic.mutations.MutationInterface;
import ni.genetic.preCalc.PreCalcInterface;
import ni.genetic.select.SelectionInterface;

import java.util.Arrays;

@SuppressWarnings("WeakerAccess")
public class Population {

    private int generation = 0;
    private final double mutationRate;
    private final int popSize, geneSize, elitism;
    private Individual[] nextGen, currGen;
    private final SelectionInterface selection;
    private final FitnessInterface fitness;
    private final BreedInterface breeder;
    private final MutationInterface mutator;
    private final PreCalcInterface preCalculations;


    public Population(
            int popSize, int geneSize,
            int elitism,
            boolean generate,
            double mutationRate,
            MutationInterface mutationInterface,
            FitnessInterface fitnessInterface,
            BreedInterface breedInterface,
            SelectionInterface selectionInterface,
            PreCalcInterface preCalculations
            )
    {
        this.preCalculations = preCalculations;
        this.currGen = new Individual[popSize];
        this.nextGen = new Individual[popSize];
        for (int i = 0; i < popSize; i++) {
            currGen[i] = new Individual(geneSize, generate);
            nextGen[i] = new Individual(geneSize, false);
        }

        this.popSize = popSize;
        this.geneSize = geneSize;
        this.elitism = elitism;
        this.mutationRate = mutationRate;
        this.mutator = mutationInterface;
        this.fitness = fitnessInterface;
        this.breeder = breedInterface;
        this.selection = selectionInterface;

        calculateFitness(preCalculations.calc(currGen));
    }

    public void evolve() {
        generation++;
        int[] preCalc = preCalculations.calc(currGen);
        for(int i = 0; i < elitism; i++) {
            nextGen[i].fitness = (currGen[i].fitness);
            System.arraycopy(currGen[i].genes, 0, nextGen[i].genes, 0, geneSize);
        }

        for (int i = Math.max(0, elitism); i < popSize; i++) {
            breeder.breed(preCalc, selection.select(preCalc, currGen), selection.select(preCalc, currGen), nextGen[i]);
            mutator.mutate(preCalc, nextGen[i], mutationRate);
            nextGen[i].fitness = Integer.MIN_VALUE;
        }

        Individual[] temp = currGen;
        currGen = nextGen;
        nextGen = temp;

        calculateFitness(preCalc);
    }

    public int evolve(int goal) {
        return evolve(goal, Integer.MAX_VALUE);
    }

    public int evolve(int goal, int max) {
        while(currGen[0].fitness < goal && generation < max)
            evolve();
        return generation;
    }

    void calculateFitness(int[] preCalc) {
        for (Individual individual : currGen)
            if(individual.fitness == Integer.MIN_VALUE)
                individual.fitness = fitness.calc(preCalc, individual);
        Arrays.sort(currGen, (a, b) -> b.fitness - a.fitness);
    }

    public Individual[] getPopulation() {
        return currGen;
    }

    public Individual getBest() {
        return currGen[0];
    }
}
