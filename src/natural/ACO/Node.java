package natural.ACO;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

public class Node {

    private static final Random random = new Random();
    static int nextUsagePoint = 1;
    int[] lastUsage;
    private static int NEXTID = 0;

    private Edge[] edges;

    private final int id = NEXTID++;
    public final String name;
    private HashMap<Node, Integer> edgeMap = new HashMap<>();

    public Node(String name, int maxThreads) {
        this.name = name;
        this.lastUsage = new int[maxThreads];
    }

    public void setMaxEdges(int n){
        this.edges = new Edge[n];
    }

    public void setEdge(Edge edge, int i) {
        edges[i] = edge;
        edgeMap.put(edge.target, i);
    }

    public void initChances() {
        double[] bias = new double[edges.length];
        Arrays.fill(bias, 1D);
        initChances(bias);
    }

    public void initChances(double[] bias) {
        double sum = 0;
        for (double bia : bias) sum += bia;
        for (int i = 0; i < bias.length; i++)
            edges[i].chance = bias[i] / sum;
    }

    public void movePercentageTo(double percentage, int id) {
        double taking = 0D;
        for (int i = 0; i < edges.length; i++) {
            if (edges[i].target.id == id) {
                id = i;
                continue;
            }
            double takes = edges[i].chance * percentage;
            edges[i].chance -= takes;
            taking += takes;
        }
        edges[id].chance += taking;
    }

    public int getRandom(int currUsage) {
        return getRandom(currUsage, 0);
    }

    public int getRandom(int currUsage, int thread) {
        double pick = 0D;
        for (int i = 0; i < edges.length; i++)
            if (edges[i].target.lastUsage[thread] != currUsage)
                pick += edges[i].chance;
        pick *= random.nextDouble();
        for (int i = 0; i < edges.length; i++) {
            if (edges[i].target.lastUsage[thread] == currUsage) continue;
            pick -= edges[i].chance;
            if (pick <= 0) return i;
        }
        return -1;
    }

    public Node getTarget(int index) {
        return edges[index].target;
    }

    public double getCost(int index) {
        return edges[index].cost;
    }

    public String getName(int index) {
        return edges[index].name;
    }

    public Edge[] getEdges() {
        return edges;
    }

    public String toString() {
        return name + ": " + Arrays.toString(Arrays.stream(edges).map(i -> i.name).toArray());
    }

    public double[] getChances() {
        return Arrays.stream(edges).mapToDouble(i -> i.chance).toArray();
    }

    public String[] getNames() {
        return Arrays.stream(edges).map(i -> i.name).toArray(String[]::new);
    }

    public double[] getCosts() {
        return Arrays.stream(edges).mapToDouble(i -> i.cost).toArray();
    }

    public int getId() {
        return id;
    }

    public static void resetIdCounter() {
        NEXTID = 0;
    }

    public void setChances(Node node) {
        for(int i = 0; i < edges.length; i++)
            edges[i].chance = node.edges[i].chance;
    }

    public Edge getEdge(int pick) {
        return edges[pick];
    }

    public int getIndex(Node target) {
        return edgeMap.get(target);
    }

    public Edge getEdge(Node target) {
        return edges[edgeMap.get(target)];
    }
}
