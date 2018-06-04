package natural.ACO;

import natural.AbstractIndividual;
import natural.AbstractPopulation;
import natural.PreCalcs;
import natural.interfaces.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class Colony extends AbstractPopulation {
    private double meanFitness = 0D;
    protected final Node[] graph;
    protected final Visitation visitation;
    protected final Fitness fitness;
    protected final Ant best;
    protected Ant bestFromGeneration;
    protected final Ant curr;
    protected final int generationSize;
    protected final PhermonePlacer pheromone;
    protected final StartingPoint start;
    protected final Mutator mutator;
    
    public Colony(int generationSize, Node[] graph, Visitation visitation, Fitness fitness, Mutator mutator, PhermonePlacer pheromone) {
        this(Runtime.getRuntime().availableProcessors(), generationSize, graph, visitation, fitness, mutator, Starts.first(), pheromone, PreCalcs.none());
    }

    public Colony(int maxThreads, int generationSize, Node[] graph, Visitation visitation, Fitness fitness, Mutator mutator, PhermonePlacer pheromone) {
        this(maxThreads, generationSize, graph, visitation, fitness, mutator, Starts.first(), pheromone, PreCalcs.none());
    }

    public Colony(
            int maxThreads, int generationSize, Node[] graph, Visitation visitation, Fitness fitness, Mutator mutator, StartingPoint start, PhermonePlacer pheromone,
            PreCalc preCalc
    ) {
        super(maxThreads, generationSize, preCalc);
        this.pheromone = pheromone;
        this.visitation = visitation;
        this.mutator = mutator;
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
    public Ant[] getPopulation() {
        return new Ant[]{best};
    }

    /**
     * The colony does not actually keep a population in memory
     * It only keeps the best found
     * @param index, ignored
     * @return best ant/solution
     */
    @Override
    public Ant getIndividual(int index) {
        return best;
    }

    @Override
    public void evolve() {
        double meanFitness = 0D;
        generation++;
        bestFromGeneration.resetFitness();
        Node start = this.start.getNode(graph);
        long usage = Node.nextUsagePoint;
        for (int i = 0; i < generationSize; i++, usage++) {
            curr.resetInsertion();
            Node at = start;
            int pick;
            while ((pick = at.getRandom(usage)) != -1) {
                curr.add(at.getEdge(pick));
                visitation.handleVisitation(memory, usage, curr, at, pick, 0);
                at = at.getEdge(pick).target;
            }
            mutator.mutate(memory, curr);
            fitness.calc(memory, curr);
            meanFitness += curr.getFitness();
            if (curr.getFitness() > bestFromGeneration.getFitness())
                bestFromGeneration.copy(curr);
        }
        Node.nextUsagePoint = usage;
        pheromone.alter(memory, bestFromGeneration);
        if (bestFromGeneration.getFitness() > best.getFitness())
            best.copy(bestFromGeneration);
        this.meanFitness = meanFitness / generationSize;
    }

    @Override
    public void evolveParallel() throws InterruptedException {
        double meanFitness = 0D;
        generation++;
        CountDownLatch lock = new CountDownLatch(maxThreads);
        Node start = this.start.getNode(graph);
        for (int i = 0, j = 0; i < generationSize; i += threadWork, j++) {
            // Secure info for the thread to have as final anchor points
            final int min = i, max = Math.min(generationSize, i + threadWork);
            final int threadId = j;
            final long myUsage = Node.nextUsagePoint + min;
            pool.submit(() -> {
                Ant bestFromGeneration = new Ant(8);
                Ant curr = new Ant(8);
                for (int k = min, q = 0; k < max; k++, q++) {
                    curr.resetInsertion();
                    Node at = start;
                    int pick;
                    while ((pick = at.getRandom(myUsage + q, threadId)) != -1) {
                        curr.add(at.getEdge(pick));
                        visitation.handleVisitation(memory, myUsage + q, curr, at, pick, threadId);
                        at = at.getEdge(pick).target;
                    }
                    mutator.mutate(memory, curr);
                    fitness.calc(memory, curr);
                    if (curr.getFitness() > bestFromGeneration.getFitness())
                        bestFromGeneration.copy(curr);
                }
                updateBestFromGen(bestFromGeneration);
                lock.countDown();
            });
        }
        lock.await(1000, TimeUnit.MINUTES);
        Node.nextUsagePoint += generationSize;
        pheromone.alter(memory, bestFromGeneration);
        if (bestFromGeneration.getFitness() > best.getFitness())
            best.copy(bestFromGeneration);
        this.meanFitness = meanFitness / generationSize;
    }

    private synchronized void updateBestFromGen(Ant ant) {
        this.meanFitness += ant.getFitness();
        if(ant.getFitness() > bestFromGeneration.getFitness())
            bestFromGeneration.copy(ant);
    }

    @Override
    public long getBestFitness() {
        return best.getFitness();
    }

    @Override
    public double getMeanFitness() {
        return meanFitness;
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
