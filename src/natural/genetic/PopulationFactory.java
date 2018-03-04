package natural.genetic;

import natural.genetic.crossover.Crossover;
import natural.genetic.fitness.Fitness;
import natural.genetic.mutations.Mutation;
import natural.genetic.preCalc.PreCalcs;
import natural.genetic.select.Selection;

public class PopulationFactory {

    public static Population oneMax(int geneSize) {
        return new Population(
                2, geneSize, 1, false, 1,
                Mutation.flipOne(),
                Fitness.oneMax(),
                Crossover.none(),
                Selection.best(),
                PreCalcs.none()
        );
    }

}
