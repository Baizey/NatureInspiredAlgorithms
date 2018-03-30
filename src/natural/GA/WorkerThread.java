package natural.GA;

import natural.GA.crossover.CrossoverInterface;
import natural.GA.fitness.FitnessInterface;
import natural.GA.mutations.MutationInterface;
import natural.GA.preCalc.PreCalcData;
import natural.GA.select.SelectionInterface;

public class WorkerThread extends Thread {

    private final Population population;
    private final int startIndex, endIndex;
    private final PreCalcData preCalc;

    WorkerThread(Population population, PreCalcData preCalc, int startIndex, int endIndex) {
        super();
        this.population = population;
        this.preCalc = preCalc;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
    }

    @Override
    public void run() {
        SelectionInterface selection = population.getSelectionInterface();
        CrossoverInterface crossover = population.getCrossover();
        MutationInterface mutator = population.getMutation();
        FitnessInterface fitness = population.getFitness();
        Individual[] currGen = population.getPopulation();
        Individual[] nextGen = population.getNextGen();
        for(int i = startIndex; i < endIndex; i++) {
            Individual male = selection.select(preCalc, currGen);
            Individual female = selection.select(preCalc, currGen);
            crossover.crossover(preCalc, male, female, nextGen[i]);
            mutator.mutate(preCalc, nextGen[i]);
            fitness.calc(nextGen[i]);
        }
    }

}
