package natural.factory;

import natural.ACO.Colony;
import natural.ACO.Fitness;
import natural.ACO.Visitations;
import natural.interfaces.AntMutation;
import natural.interfaces.Bias;

@SuppressWarnings({"WeakerAccess", "unused"})
public class ColonyFactory {
    public static Colony travelingSalesman(double[][] points, int generationSize, double percentChange, int maxThreads, Bias bias, AntMutation mutation) {
        return new Colony(maxThreads, generationSize, percentChange,
                GraphFactory.travelingSalesMan(maxThreads, points, bias),
                Visitations.addCurrentNode(),
                Fitness.lowestCostAllNodesPath(),
                mutation);
    }
}
