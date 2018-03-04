package natural.genetic;

import natural.AbstractPopulation;
import natural.genetic.crossover.CrossoverInterface;
import natural.genetic.fitness.FitnessInterface;
import natural.genetic.mutations.MutationInterface;
import natural.genetic.preCalc.PreCalcInterface;
import natural.genetic.select.SelectionInterface;

@SuppressWarnings("WeakerAccess")
public class Population extends AbstractPopulation {
    private final double mutationRate;
    private final int popSize, geneSize, elitism;
    private Individual[] nextGen, currGen;
    private final SelectionInterface selection;
    private final FitnessInterface fitness;
    private final CrossoverInterface breeder;
    private final MutationInterface mutator;
    private final PreCalcInterface preCalculations;


    public Population(
            int popSize, int geneSize,
            int elitism,
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
        this.geneSize = geneSize;
        this.elitism = elitism;
        this.mutationRate = mutationRate;
        this.mutator = mutationInterface;
        this.fitness = fitnessInterface;
        this.breeder = crossoverInterface;
        this.selection = selectionInterface;

        calculateFitness(preCalculations.calc(currGen));
    }

    @Override
    public void evolve() {
        generation++;
        double[] preCalc = preCalculations.calc(currGen);
        for(int i = 0; i < elitism; i++) {
            nextGen[i].setFitness(currGen[i]);
            nextGen[i].copyDnaFrom(currGen[i]);
        }

        for (int i = elitism; i < popSize; i++) {
            breeder.breed(preCalc,
                    selection.select(preCalc, currGen),
                    selection.select(preCalc, currGen),
                    nextGen[i]);
            mutator.mutate(preCalc, nextGen[i], mutationRate);
            nextGen[i].resetFitness();
        }

        Individual[] temp = currGen;
        currGen = nextGen;
        nextGen = temp;

        calculateFitness(preCalc);
    }

    @Override
    public double getBestFitness() {
        return currGen[0].getFitness();
    }

    void calculateFitness(double[] preCalc) {
        for (Individual individual : currGen)
            if(individual.needsFitnessCalculation())
                individual.setFitness(fitness.calc(preCalc, individual));

        // TODO: optimize this
        if(currGen[1].getFitness() > currGen[0].getFitness()) {
            Individual temp = currGen[0];
            currGen[0] = currGen[1];
            currGen[1] = temp;
        }

        //Arrays.sort(currGen, (a, b) -> b.fitness - a.fitness);
    }

    public Individual[] getPopulation() {
        return currGen;
    }

    public Individual getBest() {
        return currGen[0];
    }

    public int getGeneration() {
        return generation;
    }
}
