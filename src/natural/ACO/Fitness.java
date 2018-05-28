package natural.ACO;

public class Fitness {

    public static natural.interfaces.Fitness oneMax() {
        return (individual) -> {
            int fitness = 0;
            Ant ant = (Ant) individual;
            Node at = ant.getFirstNode();
            for (int i = 0; i < ant.getInsertionCount(); i++) {
                if (at.getName(ant.getChoiceId(i)).charAt(0) == '1') fitness++;
                at = at.getTarget(ant.getChoiceId(i));
            }
            ant.setFitness(fitness);
        };
    }

    public static natural.interfaces.Fitness mostNodesTraversed() {
        return (ant) -> ant.setFitness(((Ant)ant).getInsertionCount());
    }

    public static natural.interfaces.Fitness lowestCostAllNodesPath() {
        return (individual) -> {
            Ant ant = (Ant) individual;
            if (ant.getInsertionCount() < ant.getFirstNode().getEdges().length) {
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
