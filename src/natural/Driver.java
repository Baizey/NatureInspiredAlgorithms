package natural;

import lsm.helpers.IO.write.text.TextWriter;
import lsm.helpers.IO.write.text.console.Note;
import lsm.helpers.Time;
import natural.factory.PopulationFactory;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Arrays;

public class Driver {
    public static void main (String[] args) throws IOException {
        /*
        int i = 1000;
        AbstractPopulation population = PopulationFactory.oneMax(i);
        Time.takeTime(() -> population.evolveUntilGoal(i));
        Note.writenl(population.getBestFitness());
        /**/
        BufferedWriter writer = TextWriter.getWriter("output2", "txt", true);
        for (int geneSize = 100; geneSize <= 1000000; geneSize += 100) {
            int samples = 10;
            double[] times = new double[samples];
            for (int i = 0; i < samples; i++) {
                AbstractPopulation population = PopulationFactory.oneMax(geneSize);
                population.evolveUntilNoProgress(geneSize);
                times[i] = population.getGeneration();
            }
            Arrays.sort(times);
            writer.write(geneSize + " " + Arrays.stream(times).average().orElse(0) + "\n");
            writer.flush();
        }
        writer.close();
        /**/
    }

}
