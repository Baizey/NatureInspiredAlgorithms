package natural.ACO;

import natural.AbstractIndividual;
import natural.AbstractPopulation;
import natural.interfaces.*;
import natural.interfaces.Fitness;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class Colony extends AbstractPopulation {
    private final Node[] graph;
    private final Visitation visitation;
    private final Fitness fitness;
    private final Ant best;
    private Ant bestFromGeneration;
    private final Ant curr;
    private final int generationSize;
    private final PhermonePlacer pheromone;
    private final StartingPoint start;
    private final AntMutation mutation;
    
    public Colony(int generationSize, Node[] graph, Visitation visitation, Fitness fitness, AntMutation mutation, PhermonePlacer pheromone) {
        this(Runtime.getRuntime().availableProcessors(), generationSize, graph, visitation, fitness, mutation, Starts.first(), pheromone);
    }

    public Colony(int maxThreads, int generationSize, Node[] graph, Visitation visitation, Fitness fitness, AntMutation mutation, PhermonePlacer pheromone) {
        this(maxThreads, generationSize, graph, visitation, fitness, mutation, Starts.first(), pheromone);
    }

    public Colony(int maxThreads, int generationSize, Node[] graph, Visitation visitation, Fitness fitness, AntMutation mutation, StartingPoint start, PhermonePlacer pheromone) {
        super(maxThreads, generationSize);
        this.pheromone = pheromone;
        this.visitation = visitation;
        this.mutation = mutation;
        this.fitness = fitness;
        this.generationSize = generationSize;
        this.graph = graph;
        this.start = start;
        this.best = new Ant(8);
        this.bestFromGeneration = new Ant(8);
        this.curr = new Ant(8);
    }

    @Override
    public void copyPopulation(AbstractPopulation other) {
        var colony = (Colony) other;
        for (int i = 0; i < graph.length; i++)
            graph[i].setChances(colony.graph[i]);
    }

    @Override
    public void evolve() {
        generation++;
        bestFromGeneration.resetFitness();
        Node start = this.start.getNode(graph);
        int usage = Node.nextUsagePoint;
        for (int i = 0; i < generationSize; i++, usage++) {
            curr.resetInsertion();
            Node at = start;
            int pick;
            while ((pick = at.getRandom(usage)) != -1) {
                curr.add(at.getEdge(pick));
                visitation.handleVisitation(usage, curr, at, pick, 0);
                at = at.getEdge(pick).target;
            }
            mutation.mutate(curr);
            fitness.calc(curr);
            if (curr.getFitness() > bestFromGeneration.getFitness())
                bestFromGeneration.copy(curr);
        }
        Node.nextUsagePoint = usage;
        pheromone.alter(bestFromGeneration);
        if (bestFromGeneration.getFitness() > best.getFitness())
            best.copy(bestFromGeneration);
    }

    @Override
    public void evolveParallel() throws InterruptedException {
        generation++;
        CountDownLatch lock = new CountDownLatch(maxThreads);
        Node start = this.start.getNode(graph);
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
                    fitness.calc(curr);
                    if (curr.getFitness() > bestFromGeneration.getFitness())
                        bestFromGeneration.copy(curr);
                }
                updateBestFromGen(bestFromGeneration);
                lock.countDown();
            });
        }
        lock.await(1000, TimeUnit.MINUTES);
        Node.nextUsagePoint += generationSize;
        pheromone.alter(bestFromGeneration);
        if (bestFromGeneration.getFitness() > best.getFitness())
            best.copy(bestFromGeneration);
    }

    private synchronized void updateBestFromGen(Ant ant) {
        if(ant.getFitness() > bestFromGeneration.getFitness())
            bestFromGeneration.copy(ant);
    }

    private void promoteBestRoute() {
        for(int i = 0; i < bestFromGeneration.getInsertionCount(); i++) {
            Edge edge = bestFromGeneration.getEdge(i);
            pheromone.alter(bestFromGeneration);
        }
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
