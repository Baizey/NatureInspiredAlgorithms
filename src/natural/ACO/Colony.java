package natural.ACO;

import natural.ACO.fitness.FitnessInterface;
import natural.ACO.visitation.VisitationInterface;
import natural.AbstractIndividual;
import natural.AbstractPopulation;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class Colony extends AbstractPopulation {
    private final Node[] graph;
    private final VisitationInterface visitation;
    private final FitnessInterface fitness;
    private final Ant best;
    private Ant bestFromGeneration;
    private final Ant curr;
    private final int generationSize;
    private final Node start;
    private final double weightAltering;

    public Colony(int generationSize, double weightAltering, Node[] graph, VisitationInterface visitation, FitnessInterface fitness) {
        super(generationSize);
        this.weightAltering = weightAltering;
        this.visitation = visitation;
        this.fitness = fitness;
        this.generationSize = generationSize;
        best = new Ant(8);
        bestFromGeneration = new Ant(8);
        curr = new Ant(8);
        this.graph = graph;
        this.start = this.graph[0];
    }

    public Colony(int maxThreads, int generationSize, double weightAltering, Node[] graph, VisitationInterface visitation, FitnessInterface fitness) {
        super(maxThreads, generationSize);
        this.weightAltering = weightAltering;
        this.visitation = visitation;
        this.fitness = fitness;
        this.generationSize = generationSize;
        best = new Ant(8);
        bestFromGeneration = new Ant(8);
        curr = new Ant(8);
        this.graph = graph;
        this.start = this.graph[0];
    }

    public Colony(Colony other) {
        super(other.maxThreads, other.generationSize);
        this.weightAltering = other.weightAltering;
        this.visitation = other.visitation;
        this.fitness = other.fitness;
        this.generationSize = other.generationSize;
        best = new Ant(8);
        bestFromGeneration = new Ant(8);
        curr = new Ant(8);

        graph = new Node[other.graph.length];
        Node[] oldGraph = other.graph;

        for(int i = 0; i < graph.length; i++)
            graph[i] = new Node(oldGraph[i]);

        for(int j = 0; j < graph.length; j++){
            Node[] oldE = oldGraph[j].getEdges();
            Node[] newE = graph[j].getEdges();
            for(int k = 0; k < newE.length; k++)
                newE[k] = graph[oldE[k].getId()];
        }

        this.start = graph[0];
    }

    public void copyGraphProgression(Colony colony) {
        for (int i = 0; i < graph.length; i++)
            graph[i].setChances(colony.graph[i]);
    }

    @Override
    public void evolve() {
        generation++;
        bestFromGeneration.resetFitness();
        int usage = Node.nextUsagePoint;
        for (int i = 0; i < generationSize; i++) {
            curr.resetInsertion();
            Node at = start;
            int pick;
            while ((pick = at.getRandom(usage)) != -1) {
                curr.add(pick);
                visitation.handleVisitation(usage, curr, at, pick, 0);
                at = at.getNode(pick);
            }
            usage++;
            fitness.calc(curr, start);
            if (curr.getFitness() > bestFromGeneration.getFitness())
                bestFromGeneration.copyFrom(curr);
        }
        Node.nextUsagePoint = usage;
        promoteBestRoute();
    }

    /**
     * Threads be cray cray
     * @throws InterruptedException
     */
    @Override
    public void evolveParallel() throws InterruptedException {
        generation++;
        CountDownLatch lock = new CountDownLatch(maxThreads);
        for (int i = 0, j = 0; i < generationSize; i += threadWork, j++) {
            // Secure info for the thread to have as final anchor points
            final int min = i, max = Math.min(generationSize, i + threadWork);
            final int threadId = j;
            final int myUsage = Node.nextUsagePoint + min;
            pool.submit(() -> {
                Ant bestFromGeneration = new Ant(8);
                Ant curr = new Ant(8);
                Node at;
                for (int k = min, q = 0; k < max; k++, q++) {
                    curr.resetInsertion();
                    at = start;
                    int pick;
                    while ((pick = at.getRandom(myUsage + q, threadId)) != -1) {
                        curr.add(pick);
                        visitation.handleVisitation(myUsage + q, curr, at, pick, threadId);
                        at = at.getNode(pick);
                    }
                    fitness.calc(curr, start);
                    if (curr.getFitness() > bestFromGeneration.getFitness())
                        bestFromGeneration.copyFrom(curr);
                }
                updateBestFromGen(bestFromGeneration);
                lock.countDown();
            });
        }
        lock.await(1000, TimeUnit.MINUTES);
        Node.nextUsagePoint += generationSize;
        promoteBestRoute();
    }

    private synchronized void updateBestFromGen(Ant ant) {
        if(ant.getFitness() > bestFromGeneration.getFitness())
            bestFromGeneration.copyFrom(ant);
    }

    private void promoteBestRoute() {
        Node at = start;
        int insertions = bestFromGeneration.getInsertionCount();
        for (int i = 0; i < insertions; i++) {
            int choice = bestFromGeneration.getChoice(i);
            at.movePercentageTo(weightAltering, choice);
            at = at.getNode(choice);
        }
        if (bestFromGeneration.getFitness() > best.getFitness())
            best.copyFrom(bestFromGeneration);
    }

    public String[] edgePath() {
        int[] route = best.getUsedDna();
        Node at = start;
        String[] result = new String[route.length];
        for (int i = 0; i < best.getInsertionCount(); i++) {
            result[i] = at.getName(route[i]);
            at = at.getNode(route[i]);
        }
        return result;
    }

    public String[] nodePath() {
        int[] route = best.getUsedDna();
        Node at = start;
        String[] result = new String[route.length + 1];
        result[0] = at.name;
        for (int i = 0; i < route.length; i++) {
            at = at.getNode(route[i]);
            result[i + 1] = at.name;
        }
        return result;
    }

    @Override
    public long getBestFitness() {
        return best.getFitness();
    }

    @Override
    public AbstractIndividual getBest() {
        return best;
    }

    public Node[] getGraph() {
        return graph;
    }
}
