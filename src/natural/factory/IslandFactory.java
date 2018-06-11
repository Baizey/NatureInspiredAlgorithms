package natural.factory;

import natural.ACO.Colony;
import natural.genericGA.binaryGA.BinaryPopulation;
import natural.interfaces.Bias;
import natural.interfaces.Mutator;
import natural.islands.Convergence;
import natural.islands.Islands;

import java.util.stream.IntStream;

public class IslandFactory {

    public static Islands islandsOfTSPColonies(
            int colonies,
            int convergencePoint,
            boolean circle,
            double[][] graph,
            int generationSize,
            double minPheromone,
            double maxPheromone,
            double percentChange,
            int maxThreads,
            Bias bias,
            Mutator mutation
        ) {
        return new Islands(
                Convergence.keepBestAfterX(convergencePoint),
                (memory, pop) -> pop.evolve(5),
                IntStream.generate(() -> 0).limit(colonies).mapToObj(i -> ColonyFactory.travelingSalesman(graph, generationSize, minPheromone, maxPheromone, percentChange, maxThreads, bias, mutation)).toArray(Colony[]::new)
        );
    }

    public static Islands islandsOfPopulations(BinaryPopulation originalPopulation, int populations, int convergencePoint) {
        return new Islands(
                Convergence.keepBestAfterX(convergencePoint),
                (memory, pop) -> pop.evolve(),
                IntStream.generate(() -> 0).limit(populations).mapToObj(i -> new BinaryPopulation(originalPopulation)).toArray(BinaryPopulation[]::new)
        );
    }

}
