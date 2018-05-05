package natural.factory;

import natural.ACO.Colony;
import natural.ACO.Fitness;
import natural.ACO.NodeBias;
import natural.ACO.Visitations;

@SuppressWarnings({"WeakerAccess", "unused"})
public class ColonyFactory {

    public static Colony oneMaxBinary(int size, int generationSize, double percentChange) {
        return oneMaxBinary(Runtime.getRuntime().availableProcessors(), size, generationSize, percentChange);
    }
    public static Colony oneMaxBinary(int maxThreads, int size, int generationSize, double percentChange) {
        return new Colony(generationSize, percentChange,
                GraphFactory.binaryString(maxThreads, size),
                Visitations.addCurrentNode(),
                Fitness.oneMax());
    }

    public static Colony snakeInTheBox(int maxThreads, int dimensions, int generationSize, double percentChange) {
        return new Colony(generationSize, percentChange,
                GraphFactory.snakeInTheBox(maxThreads, dimensions),
                Visitations.addCurrentAndEdgeNodes(),
                Fitness.mostNodesTraversed());
    }

    public static Colony travelingSalesman(boolean circlePath, double[][] points, int generationSize, double percentChange, int maxThreads) {
        if(circlePath)
            return travelingSalesmanCircle(points, generationSize, percentChange, maxThreads);
        else
            return travelingSalesmanPath(points, generationSize, percentChange, maxThreads);
    }

    public static Colony travelingSalesmanCircle(double[][] points, int generationSize, double percentChange) {
        return travelingSalesmanCircle(points, generationSize, percentChange, Runtime.getRuntime().availableProcessors());
    }
    public static Colony travelingSalesmanCircle(double[][] points, int generationSize, double percentChange, int maxThreads) {
        return new Colony(maxThreads, generationSize, percentChange,
                GraphFactory.travelingSalesMan(maxThreads, points, NodeBias.polynomialBias()),
                Visitations.addCurrentNode(),
                Fitness.lowestCostAllNodesCircle());
    }

    public static Colony travelingSalesmanPath(double[][] points, int generationSize, double percentChange) {
        return travelingSalesmanPath(points, generationSize, percentChange, Runtime.getRuntime().availableProcessors());
    }
    public static Colony travelingSalesmanPath(double[][] points, int generationSize, double percentChange, int maxThreads) {
        return new Colony(maxThreads, generationSize, percentChange,
                GraphFactory.travelingSalesMan(maxThreads, points, NodeBias.polynomialBias()),
                Visitations.addCurrentNode(),
                Fitness.lowestCostAllNodesPath());
    }

}
