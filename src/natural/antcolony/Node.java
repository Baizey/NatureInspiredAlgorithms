package natural.antcolony;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@SuppressWarnings({"unused", "WeakerAccess"})
public class Node {
    private static final Random random = ThreadLocalRandom.current();
    static final int lastUsageStartingPoint = 1;
    public int lastUsage = 0;
    private static int nextId = 0;
    private int nextEdge = 0;
    private Node[] nodes;
    private double[] costs, chances;
    private String[] names;
    public final String name;

    public Node(int edges, String name) {
        this.nodes = new Node[edges];
        this.chances = new double[edges];
        this.costs = new double[edges];
        this.names = new String[edges];
        this.name = name;
    }

    public Node(int edges){
        this.nodes = new Node[edges];
        this.chances = new double[edges];
        this.costs = new double[edges];
        this.names = new String[edges];
        this.name = Integer.toString(nextId++);
    }

    public void addEdge(Node edge, double cost) {
        addEdge(edge, cost, edge.name);
    }

    public void addEdge(Node edge, String name) {
        addEdge(edge, 0D, name);
    }

    public void addEdge(Node node, double cost, String name) {
        costs[nextEdge] = cost;
        names[nextEdge] = name;
        nodes[nextEdge++] = node;
    }

    public void initChances() {
        double[] bias = new double[nodes.length];
        Arrays.fill(bias, 1D);
        initChances(bias);
    }

    public void initChances(double[] bias) {
        double sum = 0;
        for (double bia : bias) sum += bia;
        for(int i = 0; i < bias.length; i++)
            chances[i] = bias[i] / sum;
    }

    public void movePercentageTo(double percentage, int index) {
        double taking = 0D;
        for (int i = 0; i < chances.length; i++) {
            if (i == index) continue;
            double takes = chances[i] * percentage;
            chances[i] -= takes;
            taking += takes;
        }
        chances[index] += taking;
    }

    private static final HashSet<Node> EMPTY = new HashSet<>();

    public int getRandom(int currUsage) {
        double pick = 0D;
        for(int i = 0; i < chances.length; i++)
            if(nodes[i].lastUsage != currUsage)
                pick += chances[i];
        pick *= random.nextDouble();
        for (int i = 0; i < chances.length; i++) {
            if(nodes[i].lastUsage == currUsage) continue;
            pick -= chances[i];
            if (pick <= 0) return i;
        }
        return -1;
    }

    public void killChance(int index) {
        chances[index] = 0D;
    }

    public Node getNode(int index) {
        return nodes[index];
    }
    public double getCost(int index) {
        return costs[index];
    }
    public String getName(int index) {
        return names[index];
    }

    public Node[] getNodes() {
        return nodes;
    }

    public String toString(){
        return name + ": " + Arrays.toString(names);
    }

    public double[] getChances() {
        return chances;
    }

    public String[] getNames() {
        return names;
    }

    public double[] getCosts() {
        return costs;
    }
}