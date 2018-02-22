package ni;

import ni.genetic.Population;
import ni.genetic.breed.BreedInterface;
import ni.genetic.breed.Breeding;
import ni.genetic.fitness.FitnessInterface;
import ni.genetic.mutations.Mutation;
import ni.genetic.mutations.MutationInterface;
import ni.genetic.preCalc.PreCalcInterface;
import ni.genetic.preCalc.PreCalcs;
import ni.genetic.select.Selection;
import ni.genetic.select.SelectionInterface;

public class NaturalFactory {

    public static Population createPopulation(int geneSize, FitnessInterface fitness){
        return new Population(2, geneSize, 1, true, 1, Mutation.flipOne(), fitness, Breeding.none(), Selection.best(), PreCalcs.none());
    }

    public static Population createPopulation(
            int popSize,
            int geneSize,
            int elitism,
            boolean generate,
            double mutationRate,
            MutationInterface mutation,
            FitnessInterface fitness,
            BreedInterface breeding,
            SelectionInterface selection,
            PreCalcInterface precalc
    ){
        return new Population(popSize, geneSize, elitism, generate, mutationRate, mutation, fitness, breeding, selection, precalc);
    }


}
