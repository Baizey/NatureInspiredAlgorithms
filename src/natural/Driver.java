package natural;

import lsm.helpers.Time;
import natural.factory.PopulationFactory;

public class Driver {
    public static void main(String... args) throws Exception {
        int genes = 100000;
        AbstractPopulation population = PopulationFactory.oneMax(genes);
        Time.init();
        population.evolveUntilGoal(genes);
        Time.write();

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
