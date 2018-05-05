package natural;

import lsm.helpers.IO.write.text.console.Note;
import lsm.helpers.Time;
import natural.GA.Population;
import natural.factory.IslandFactory;
import natural.factory.PopulationFactory;

public class Driver {
    @SuppressWarnings("RedundantThrows")
    public static void main (String[] args) throws Exception {
        int genes = 1000;
        AbstractPopulation initialPopulation = PopulationFactory.oneMax(genes);

        AbstractPopulation population = IslandFactory.islandsOfPopulations((Population) initialPopulation, 6, 100);

        Note.writenl(initialPopulation.getMaxThreads());
        Note.writenl(initialPopulation.getThreadWork());
        Time.takeTime(() -> population.evolveUntilGoal(genes, () -> Note.writenl(population.getBestFitness())));
    }

}
