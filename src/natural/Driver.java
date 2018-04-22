package natural;

import lsm.helpers.IO.write.text.console.Note;
import lsm.helpers.Time;
import natural.GA.Population;
import natural.GA.fitness.FitnessInterface;
import natural.factory.PopulationFactory;

public class Driver {
    @SuppressWarnings("RedundantThrows")
    public static void main (String[] args) throws Exception {

        Islands islands = new Islands(
                abstractPops -> {
                    Population[] pops = (Population[]) abstractPops;
                    int best = 0;
                    for(int i = 1; i < pops.length; i++)
                        if(pops[i].getBestFitness() > pops[best].getBestFitness())
                            best = i;
                    for(int i = 0; i < pops.length; i++)
                        if(i != best) pops[i].copyPopulationDnaFrom(pops[best]);
                },
                island -> { try { island.evolve(10); } catch (InterruptedException ignored) { } },
                PopulationFactory.oneMax(100),
                PopulationFactory.oneMax(100),
                PopulationFactory.oneMax(100),
                PopulationFactory.oneMax(100),
                PopulationFactory.oneMax(100),
                PopulationFactory.oneMax(100));


        /**/
        int goal = 10000;
        int genes = 150;
        FitnessInterface fitness = (pop) -> {
            int sum = 0;
            for(int i = 0; i < pop.getLength(); i++)
                if(pop.getDna().get(i)) sum += i;
            pop.setFitness((long)(Integer.MAX_VALUE - Math.abs(sum - goal)));
        };

        Population population = PopulationFactory.normalPopulation(genes, fitness);
        Population finalPopulation = population;
        Time.takeTime(() -> {
            try {
                finalPopulation.evolveUntilGoal(Integer.MAX_VALUE);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        Note.writenl(population.getBestFitness());
        Note.writenl((population).getBest());

        population = PopulationFactory.normalPopulation(genes, fitness);
        Population finalPopulation1 = population;
        Time.init();
        finalPopulation1.evolveUntilGoalParallel(Integer.MAX_VALUE);
        Time.write();
        Note.writenl(population.getBestFitness());
        Note.writenl(population.getBest());
        /*
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
