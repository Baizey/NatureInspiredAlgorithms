package natural.ACO;

import natural.ACO.fitness.FitnessInterface;
import natural.ACO.visitation.VisitationInterface;
import natural.AbstractPopulation;

public class Colony extends AbstractPopulation {
    private final Node[] graph;
    private int lastUsage = Node.lastUsageStartingPoint;
    private final VisitationInterface visitation;
    private final FitnessInterface fitness;
    private final Ant best, bestFromGeneration, curr;
    private final int generationSize;
    private final Node start;
    private final double weightAltering;

    public Colony(int generationSize, double weightAltering, Node[] graph, VisitationInterface visitation, FitnessInterface fitness) {
        this.weightAltering = weightAltering;
        this.visitation = visitation;
        this.fitness = fitness;
        this.generationSize = generationSize;
        best = new Ant(8);
        bestFromGeneration = new Ant(8);
        curr = new Ant(8);
        this.graph = graph;
        this.start = graph[0];
    }

    public void copyGraphProgression(Colony colony) {
        for(int i = 0; i < graph.length; i++)
            graph[i].setChances(colony.graph[i]);
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

    @Override
    public void evolveParallel() throws InterruptedException {
        evolve();
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
    public long getBestFitness() {
        return best.getFitness();
    }
}
