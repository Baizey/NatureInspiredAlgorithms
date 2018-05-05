package natural.factory;

import natural.ACO.Colony;
import natural.AbstractPopulation;
import natural.GA.Population;
import natural.islands.Convergence;
import natural.islands.Islands;

import java.util.stream.IntStream;

@SuppressWarnings("unused")
public class IslandFactory {

    public static Islands islandsOfColonies(Colony originalColony, int colonies, int convergencePoint) {
        return new Islands(
                Convergence.keepBestAfterColonyX(convergencePoint),
                AbstractPopulation::evolve,
                IntStream.generate(() -> 0).limit(colonies).mapToObj(i -> new Colony(originalColony)).toArray(Colony[]::new)
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
