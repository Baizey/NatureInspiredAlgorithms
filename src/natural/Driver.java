package natural;

import lsm.helpers.IO.write.text.console.Note;
import lsm.helpers.Time;
import natural.factory.ColonyFactory;
import natural.factory.PopulationFactory;

import java.util.HashSet;
import java.util.Set;

public class Driver {
    public static void main(String... args) {
        int genes = 100000;
        AbstractPopulation population = PopulationFactory.oneMax(genes);
        Time.takeTime(() -> population.evolveUntilGoal(genes));
        Note.writenl(population.getBestFitness());

        /*
        BufferedWriter writer = TextWriter.getWriter("output2", "txt", true);
        for (int geneSize = 100; geneSize <= 1000000; geneSize += 10) {
            int samples = 1;
            double[] times = new double[samples];
            for (int i = 0; i < samples; i++) {
                AbstractPopulation population = PopulationFactory.oneMax(geneSize);
                int finalGeneSize = geneSize;
                Time.takeTime(String.valueOf(geneSize), () -> population.evolveUntilNoProgress(finalGeneSize));
                times[i] = population.getGeneration();
            }
            Arrays.sort(times);
            writer.write(geneSize + " " + Arrays.stream(times).average().orElse(0) + "\n");
            writer.flush();
        }
        writer.close();
        */
    }

}
