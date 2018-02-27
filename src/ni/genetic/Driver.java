package ni.genetic;

import lsm.helpers.IO.write.text.TextWriter;
import lsm.helpers.Time;
import ni.genetic.breed.Breeding;
import ni.genetic.fitness.Fitness;
import ni.genetic.mutations.Mutation;
import ni.genetic.preCalc.PreCalcs;
import ni.genetic.select.Selection;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Arrays;

public class Driver {

    static String fill(String input) {
        StringBuilder sb = new StringBuilder(input);
        while(sb.length() < 64) sb.insert(0, "0");
        return sb.toString();
    }

    public static void main(String... args) throws IOException {
        double mutationRate = 1;
        int elitism = 1;
        int popSize = 2;
        boolean generation = false;
        int geneSize = 50000;

        BufferedWriter writer = TextWriter.getWriter("output", "txt", true);

        for (geneSize = 10; geneSize <= 1000000; geneSize += 10) {

            double samples = 1;
            double[] times = new double[(int) samples];

            for (int i = 0; i < samples; i++) {
                Population population = new Population(
                        popSize, geneSize, elitism, generation, mutationRate,
                        Mutation.flipOne(),
                        Fitness.oneMax(),
                        Breeding.none(),
                        Selection.best(),
                        PreCalcs.none()
                );
                int finalGeneSize = geneSize;
                Time.takeTime(String.valueOf(geneSize), () -> population.evolve(finalGeneSize));
                times[i] = population.getGeneration();
            }
            Arrays.sort(times);
            writer.write(geneSize + " " + Arrays.stream(times).average().orElse(0) + "\n");
            writer.flush();
        }
        writer.close();
    }

}
