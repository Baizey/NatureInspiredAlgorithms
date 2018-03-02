package ni.antcolony;

import ni.AbstractPopulation;
import ni.antcolony.fitness.FitnessInterface;
import ni.antcolony.visitation.VisitationInterface;

public class Colony extends AbstractPopulation {
    private int lastUsage = Node.lastUsageStartingPoint;
    private final VisitationInterface visitation;
    private final FitnessInterface fitness;
    private final Ant best, bestFromGeneration, curr;
    private final int generationSize;
    private final Node start;
    private final double weightAltering;

    public Colony(int generationSize, double weightAltering, Node start, VisitationInterface visitation, FitnessInterface fitness) {
        this.weightAltering = weightAltering;
        this.visitation = visitation;
        this.fitness = fitness;
        this.generationSize = generationSize;
        best = new Ant(8);
        bestFromGeneration = new Ant(8);
        curr = new Ant(8);
        this.start = start;
    }

    @Override
    public void evolve() {
        generation++;
        bestFromGeneration.resetFitness();
        Node at;
        for (int i = 0; i < generationSize; i++) {
            curr.resetInsertion();
            at = start;
            int pick;
            while ((pick = at.getRandom(lastUsage)) != -1) {
                curr.add(pick);
                visitation.handleVisitation(lastUsage, curr, at, pick);
                at = at.getNode(pick);
            }
            lastUsage++;
            fitness.calc(curr, start);
            if (curr.getFitness() > bestFromGeneration.getFitness())
                bestFromGeneration.copyFrom(curr);
        }

        at = start;
        int insertions = bestFromGeneration.getInsertionCount();
        for(int i = 0; i < insertions; i++) {
            int choice = bestFromGeneration.getChoice(i);
            at.movePercentageTo(weightAltering, choice);
            at = at.getNode(choice);
        }

        if (bestFromGeneration.getFitness() > best.getFitness())
            best.copyFrom(bestFromGeneration);
    }

    public String[] edgePath(){
        int[] route = best.getUsedDna();
        Node at = start;
        String[] result = new String[route.length];
        for(int i = 0; i < best.getInsertionCount(); i++) {
            result[i] = at.getName(route[i]);
            at = at.getNode(route[i]);
        }
        return result;
    }

    public String[] nodePath(){
        int[] route = best.getUsedDna();
        Node at = start;
        String[] result = new String[route.length + 1];
        result[0] = at.name;
        for(int i = 0; i < route.length; i++) {
            at = at.getNode(route[i]);
            result[i + 1] = at.name;
        }
        return result;
    }

    @Override
    public int getBestFitness() {
        return best.getFitness();
    }
}
