package natural.ACO;

import natural.interfaces.AntColonyFitness;

public class Fitness {

    public static AntColonyFitness oneMax() {
        return (ant, node) -> {
            int fitness = 0;
            Node at = node;
            for (int i = 0; i < ant.getInsertionCount(); i++) {
                if (at.getName(ant.getChoice(i)).charAt(0) == '1') fitness++;
                at = at.getNode(ant.getChoice(i));
            }
            ant.setFitness(fitness);
        };
    }

    public static AntColonyFitness mostNodesTraversed() {
        return (ant, node) -> ant.setFitness(ant.getInsertionCount());
    }

    public static AntColonyFitness lowestCostAllNodesCircle() {
        return (ant, node) -> {
            if (ant.getInsertionCount() < node.getEdges().length) {
                ant.setFitness(ant.getInsertionCount());
                return;
            }
            Node at = node;
            long totalCost = 0;
            for (int i = 0; i < ant.getInsertionCount(); i++) {
                totalCost += at.getCost(ant.getChoice(i));
                at = at.getNode(ant.getChoice(i));
            }
            totalCost += at.getCost(0);
            ant.setFitness(Long.MAX_VALUE - totalCost);
        };
    }

    public static AntColonyFitness lowestCostAllNodesPath() {
        return (ant, node) -> {
            if (ant.getInsertionCount() < node.getEdges().length) {
                ant.setFitness(ant.getInsertionCount());
                return;
            }
            Node at = node;
            long totalCost = 0;
            for (int i = 0; i < ant.getInsertionCount(); i++) {
                totalCost += at.getCost(ant.getChoice(i));
                at = at.getNode(ant.getChoice(i));
            }
            ant.setFitness(Long.MAX_VALUE - totalCost);
        };
    }
}
