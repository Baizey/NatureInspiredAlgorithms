package natural;

import natural.ACO.Ant;
import natural.genericGA.binaryGA.BinaryIndividual;
import natural.genericGA.binaryGA.Dna;
import natural.interfaces.Fitness;

public class FitnessFunctions {

    public static Fitness oneMax() {
        return ((memory, individual) -> individual.setFitness(((BinaryIndividual) individual).getSolution().cardinality()));
    }

    public static Fitness leadingOnes() {
        return ((memory, individual) -> individual.setFitness(((BinaryIndividual) individual).getSolution().leadingOnes()));
    }

    public static Fitness subsetSum(int goal, int[] nums) {
        return (memory, individual) -> {
            Dna dna = ((BinaryIndividual) individual).getSolution();
            int sum = 0;
            int totalSum = 0;
            for (int i = 0; i < nums.length; totalSum += nums[i], i++)
                if (dna.get(i))
                    sum += nums[i];
            individual.setFitness(totalSum - Math.abs(sum - goal));
        };
    }

    public static Fitness mostNodesTraversed() {
        return (memory, ant) -> ant.setFitness(((Ant) ant).getInsertionCount());
    }

    public static Fitness lowestCostAllNodesPath() {
        return (memory, individual) -> {
            Ant ant = (Ant) individual;
            if (ant.getInsertionCount() < ant.getFirstNode().getEdges().length) {
                ant.setFitness(ant.getInsertionCount());
                return;
            }
            long totalCost = Integer.MAX_VALUE;
            for (int i = 0; i < ant.getInsertionCount(); i++)
                totalCost -= ant.getEdge(i).cost;
            ant.setFitness(totalCost);
        };
    }
}
