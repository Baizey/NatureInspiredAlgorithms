package ni.antcolony;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

@SuppressWarnings({"unused", "WeakerAccess"})
public class Node<T> {
    private final ArrayList<Node> nodes = new ArrayList<>();
    private final ArrayList<T> edgeValue = new ArrayList<>();
    private double[] chances;

    public void addEdge(Node node, T value) {
        nodes.add(node);
        edgeValue.add(value);
    }

    public Node getNode(int index) {
        return nodes.get(index);
    }

    public T getValue(int index) {
        return edgeValue.get(index);
    }

    public void initChances() {
        double[] bias = new double[nodes.size()];
        Arrays.fill(bias, 1D);
        initChances(bias);
    }

    public void initChances(double[] bias) {
        double sum = Arrays.stream(bias).sum();
        chances = Arrays.stream(bias).map(value -> value / sum).toArray();
    }

    public void movePercentageTo(int index) {
        movePercentageTo(0.05D, index);
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

    public int getRandom() {
        return getRandom(EMPTY);
    }

    public int getRandom(HashSet<Node> unavailable) {
        boolean[] available = new boolean[chances.length];
        double totalAvail = 0D;
        for (int i = 0; i < available.length; i++) {
            available[i] = !unavailable.contains(nodes.get(i));
            if (available[i]) totalAvail += chances[i];
        }
        double pick = Math.random() * totalAvail;
        for (int i = 0; i < chances.length; i++) {
            if (!available[i]) continue;
            pick -= chances[i];
            if (pick <= 0) return i;
        }
        return -1;
    }

    public void killChance(int index) {
        chances[index] = 0D;
    }
}