package natural.ACO;

import natural.ACO.fitness.FitnessInterface;
import natural.ACO.visitation.VisitationInterface;
import natural.AbstractPopulation;

import java.util.concurrent.CountDownLatch;
import java.util.stream.IntStream;

public class Colony extends AbstractPopulation {
    private final Node[] graph;
    private final VisitationInterface visitation;
    private final FitnessInterface fitness;
    private final Ant best;
    private Ant bestFromGeneration;
    private final Ant curr;
    private final Ant[] bestFromGenerationParallel, currParallel;
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
        bestFromGenerationParallel = IntStream.of(8).limit(maxThreads).mapToObj(Ant::new).toArray(Ant[]::new);
        currParallel = IntStream.of(8).limit(maxThreads).mapToObj(Ant::new).toArray(Ant[]::new);
        bestFromGeneration = bestFromGenerationParallel[0];
        curr = currParallel[0];
        this.graph = graph;
        this.start = graph[0];
    }

    public Colony(int maxThreads, int generationSize, double weightAltering, Node[] graph, VisitationInterface visitation, FitnessInterface fitness) {
        super(maxThreads, generationSize);
        this.weightAltering = weightAltering;
        this.visitation = visitation;
        this.fitness = fitness;
        this.generationSize = generationSize;
        best = new Ant(8);
        bestFromGenerationParallel = IntStream.of(8).limit(maxThreads).mapToObj(Ant::new).toArray(Ant[]::new);
        currParallel = IntStream.of(8).limit(maxThreads).mapToObj(Ant::new).toArray(Ant[]::new);
        bestFromGeneration = bestFromGenerationParallel[0];
        curr = currParallel[0];
        this.graph = graph;
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
        for (int i = 0; i < generationSize; i++) {
            curr.resetInsertion();
            Node at = start;
            int pick;
            while ((pick = at.getRandom(Node.nextUsagePoint)) != -1) {
                curr.add(pick);
                visitation.handleVisitation(Node.nextUsagePoint, curr, at, pick, 0);
                at = at.getNode(pick);
            }
            Node.nextUsagePoint++;
            fitness.calc(curr, start);
            if (curr.getFitness() > bestFromGeneration.getFitness())
                bestFromGeneration.copyFrom(curr);
        }
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
            final int myUsageFinal = Node.nextUsagePoint + min;
            pool.submit(() -> {
                Ant bestFromGeneration = bestFromGenerationParallel[threadId];
                Ant curr = currParallel[threadId];
                int myUsage = myUsageFinal;
                bestFromGeneration.resetFitness();
                Node at;
                for (int k = min; k < max; k++) {
                    curr.resetInsertion();
                    at = start;
                    int pick;
                    while ((pick = at.getRandom(myUsage, threadId)) != -1) {
                        curr.add(pick);
                        visitation.handleVisitation(myUsage, curr, at, pick, threadId);
                        at = at.getNode(pick);
                    }
                    myUsage++;
                    fitness.calc(curr, start);
                    if (curr.getFitness() > bestFromGeneration.getFitness())
                        bestFromGeneration.copyFrom(curr);
                }
                lock.countDown();
            });
        }
        lock.await();
        Node.nextUsagePoint += generationSize;
        // Find best path from the different threads
        for (Ant ant : bestFromGenerationParallel)
            if (ant.getFitness() > bestFromGeneration.getFitness())
                this.bestFromGeneration = ant;
        promoteBestRoute();
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
}
