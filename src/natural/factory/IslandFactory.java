package natural.factory;

import natural.ACO.Colony;
import natural.AbstractPopulation;
import natural.GA.Population;
import natural.interfaces.AntMutation;
import natural.interfaces.Bias;
import natural.islands.Convergence;
import natural.islands.Islands;

import java.util.stream.IntStream;

@SuppressWarnings("unused")
public class IslandFactory {

    public static Islands islandsOfTSPColonies(
            int colonies,
            int convergencePoint,
            boolean circle,
            double[][] graph,
            int generationSize,
            double percentChange,
            int maxThreads,
            Bias bias,
            AntMutation mutation
        ) {
        return new Islands(
                Convergence.keepBestAfterColonyX(convergencePoint),
                AbstractPopulation::evolve,
                IntStream.generate(() -> 0).limit(colonies).mapToObj(i -> ColonyFactory.travelingSalesman(graph, generationSize, percentChange, maxThreads, bias, mutation)).toArray(Colony[]::new)
        );
    }

    public static Islands islandsOfPopulations(Population originalPopulation, int populations, int convergencePoint) {
        return new Islands(
                Convergence.keepBestAfterPopulationX(convergencePoint),
                AbstractPopulation::evolve,
                IntStream.generate(() -> 0).limit(populations).mapToObj(i -> new Population(originalPopulation)).toArray(Population[]::new)
        );
    }

}
