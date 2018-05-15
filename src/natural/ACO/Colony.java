package natural.ACO;

import natural.AbstractIndividual;
import natural.AbstractPopulation;
import natural.interfaces.AntColonyFitness;
import natural.interfaces.AntMutation;
import natural.interfaces.Visitation;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class Colony extends AbstractPopulation {
    private final Node[] graph;
    private final Visitation visitation;
    private final AntColonyFitness fitness;
    private final Ant best;
    private Ant bestFromGeneration;
    private final Ant curr;
    private final int generationSize;
    private final Node start;
    private AntMutation mutation;
    private final double weightAltering;

    public Colony(int generationSize, double weightAltering, Node[] graph, Visitation visitation, AntColonyFitness fitness) {
        super(generationSize);
        this.weightAltering = weightAltering;
        this.visitation = visitation;
        this.fitness = fitness;
        this.generationSize = generationSize;
        this.graph = graph;
        this.start = this.graph[0];
        this.best = new Ant(8);
        this.bestFromGeneration = new Ant(8);
        this.curr = new Ant(8);
    }

    public Colony(int maxThreads, int generationSize, double weightAltering, Node[] graph, Visitation visitation, AntColonyFitness fitness, AntMutation mutation) {
        super(maxThreads, generationSize);
        this.weightAltering = weightAltering;
        this.visitation = visitation;
        this.mutation = mutation;
        this.fitness = fitness;
        this.generationSize = generationSize;
        this.graph = graph;
        this.start = this.graph[0];
        this.best = new Ant(8);
        this.bestFromGeneration = new Ant(8);
        this.curr = new Ant(8);
    }

    public void copyGraphProgression(Colony colony) {
        for (int i = 0; i < graph.length; i++)
            graph[i].setChances(colony.graph[i]);
    }

    @Override
    public void evolve() {
        generation++;
        bestFromGeneration.resetFitness();
        for (int i = 0; i < generationSize; i++, Node.nextUsagePoint++) {
            curr.resetInsertion();
            Node at = start;
            int pick;
            while ((pick = at.getRandom(Node.nextUsagePoint)) != -1) {
                curr.add(at.getEdge(pick));
                visitation.handleVisitation(Node.nextUsagePoint, curr, at, pick, 0);
                at = at.getEdge(pick).target;
            }
            mutation.mutate(curr);
            fitness.calc(curr, start);
            if (curr.getFitness() > bestFromGeneration.getFitness())
                bestFromGeneration.copyFrom(curr);
        }
        promoteBestRoute();
    }

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
                for (int k = min, q = 0; k < max; k++, q++) {
                    curr.resetInsertion();
                    Node at = start;
                    int pick;
                    while ((pick = at.getRandom(myUsage + q, threadId)) != -1) {
                        curr.add(at.getEdge(pick));
                        visitation.handleVisitation(myUsage + q, curr, at, pick, threadId);
                        at = at.getEdge(pick).target;
                    }
                    mutation.mutate(curr);
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

        for(int i = 0; i < bestFromGeneration.getInsertionCount(); i++) {
            Edge edge = bestFromGeneration.getEdge(i);
            edge.source.movePercentageTo(weightAltering, edge.target.getId());
        }
        if (bestFromGeneration.getFitness() > best.getFitness())
            best.copyFrom(bestFromGeneration);
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

    public Edge[] getEdges() {
        return best.getEdges();
    }
}
