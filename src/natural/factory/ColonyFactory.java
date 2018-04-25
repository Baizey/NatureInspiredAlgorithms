package natural.factory;

import natural.ACO.Colony;
import natural.ACO.Node;
import natural.ACO.visitation.Visitations;

public class ColonyFactory {

    public static Colony oneMaxBinary(int maxThreads, int size, int generationSize, double percentChange) {
        return new Colony(generationSize, percentChange,
                GraphFactory.binaryString(maxThreads, size),
                Visitations.addCurrentNode(),
                (ant, node) -> {
                    int fitness = 0;
                    Node at = node;
                    for(int i = 0; i < ant.getInsertionCount(); i++) {
                        if(at.getName(ant.getChoice(i)).charAt(0) == '1') fitness++;
                        at = at.getNode(ant.getChoice(i));
                    }
                    ant.setFitness(fitness);
                });
    }

    public static Colony snakeInTheBox(int maxThreads, int dimensions, int generationSize, double percentChange) {
        return new Colony(generationSize, percentChange,
                GraphFactory.snakeInTheBox(maxThreads, dimensions),
                Visitations.addCurrentAndEdgeNodes(),
                (ant, node) -> ant.setFitness(ant.getInsertionCount()));
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
                GraphFactory.travelingSalesMan(maxThreads, points, true),
                Visitations.addCurrentNode(),
                (ant, node) -> {
                    if(ant.getInsertionCount() < node.getEdges().length){
                        ant.setFitness(ant.getInsertionCount());
                        return;
                    }
                    Node at = node;
                    long totalCost = 0;
                    for(int i = 0; i < ant.getInsertionCount(); i++) {
                        totalCost += at.getCost(ant.getChoice(i));
                        at = at.getNode(ant.getChoice(i));
                    }
                    totalCost += at.getCost(0);
                    ant.setFitness(Long.MAX_VALUE - totalCost);
                });
    }

    public static Colony travelingSalesmanPath(double[][] points, int generationSize, double percentChange) {
        return travelingSalesmanPath(points, generationSize, percentChange, Runtime.getRuntime().availableProcessors());
    }
    public static Colony travelingSalesmanPath(double[][] points, int generationSize, double percentChange, int maxThreads) {
        return new Colony(maxThreads, generationSize, percentChange,
                GraphFactory.travelingSalesMan(maxThreads, points, true),
                Visitations.addCurrentNode(),
                (ant, node) -> {
                    if(ant.getInsertionCount() < node.getEdges().length){
                        ant.setFitness(ant.getInsertionCount());
                        return;
                    }
                    Node at = node;
                    long totalCost = 0;
                    for(int i = 0; i < ant.getInsertionCount(); i++) {
                        totalCost += at.getCost(ant.getChoice(i));
                        at = at.getNode(ant.getChoice(i));
                    }
                    ant.setFitness(Long.MAX_VALUE - totalCost);
                });
    }

}
