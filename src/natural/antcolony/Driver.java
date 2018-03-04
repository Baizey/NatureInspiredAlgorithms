package natural.antcolony;

import lsm.helpers.IO.write.text.console.Note;
import lsm.helpers.Time;
import natural.AbstractPopulation;
import natural.genetic.PopulationFactory;

public class Driver {
    public static void main(String... args) {
        int genes = 200000;
        AbstractPopulation population = PopulationFactory.oneMax(genes);
        Time.takeTime(() -> population.evolveUntilGoal(genes));
        Note.writenl(population.getBestFitness());
    }
}
