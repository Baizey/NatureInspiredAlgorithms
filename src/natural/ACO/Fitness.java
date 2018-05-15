package natural.ACO;

import natural.interfaces.AntColonyFitness;

public class Fitness {

    public static AntColonyFitness oneMax() {
        return (ant, node) -> {
            int fitness = 0;
            Node at = node;
            for (int i = 0; i < ant.getInsertionCount(); i++) {
                if (at.getName(ant.getChoiceId(i)).charAt(0) == '1') fitness++;
                at = at.getTarget(ant.getChoiceId(i));
            }
            ant.setFitness(fitness);
        };
    }

    public static AntColonyFitness mostNodesTraversed() {
        return (ant, node) -> ant.setFitness(ant.getInsertionCount());
    }

    public static AntColonyFitness lowestCostAllNodesPath() {
        return (ant, node) -> {
            if (ant.getInsertionCount() < node.getEdges().length) {
                ant.setFitness(ant.getInsertionCount());
                return;
            }
            long totalCost = Integer.MAX_VALUE;
            for(int i = 0; i < ant.getInsertionCount(); i++)
                totalCost -= ant.getEdge(i).cost;
            ant.setFitness(totalCost);
        };
    }
}
