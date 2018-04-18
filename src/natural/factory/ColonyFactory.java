package natural.factory;

import natural.ACO.Colony;
import natural.ACO.Node;
import natural.ACO.visitation.Visitations;

public class ColonyFactory {

    public static Colony oneMaxBinary(int size, int generationSize, double percentChange) {
        return new Colony(generationSize, percentChange,
                GraphFactory.binaryString(size),
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

    public static Colony snakeInTheBox(int dimensions, int generationSize, double percentChange) {
        return new Colony(generationSize, percentChange,
                GraphFactory.snakeInTheBox(dimensions),
                Visitations.addCurrentAndEdgeNodes(),
                (ant, node) -> ant.setFitness(ant.getInsertionCount()));
    }

    public static Colony travelingSalesman(double[][] points, int generationSize, double percentChange) {
        return new Colony(generationSize, percentChange,
                GraphFactory.travelingSalesMan(points, true),
                Visitations.addCurrentNode(),
                (ant, node) -> {
                    int fitness = Integer.MAX_VALUE;
                    Node at = node;
                    for(int i = 0; i < ant.getInsertionCount(); i++) {
                        fitness -= at.getCost(ant.getChoice(i));
                        at = at.getNode(ant.getChoice(i));
                    }
                    ant.setFitness(fitness);
                });
    }

}
