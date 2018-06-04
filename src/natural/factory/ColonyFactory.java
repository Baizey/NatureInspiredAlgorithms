package natural.factory;

import natural.ACO.Colony;
import natural.ACO.Pheromones;
import natural.ACO.Visitations;
import natural.FitnessFunctions;
import natural.interfaces.Bias;
import natural.interfaces.Mutator;

@SuppressWarnings({"WeakerAccess", "unused"})
public class ColonyFactory {
    public static Colony travelingSalesman(
            double[][] points,
            int generationSize,
            double minPheromone, double maxPheromone, double percentChange,
            int maxThreads,
            Bias bias,
            Mutator mutation) {
        return new Colony(maxThreads, generationSize,
                GraphFactory.travelingSalesMan(maxThreads, points, bias),
                Visitations.addCurrentNode(),
                FitnessFunctions.lowestCostAllNodesPath(),
                mutation,
                Pheromones.MMAS(minPheromone, maxPheromone, percentChange));
    }
}
