package natural.factory;

import natural.ACO.Node;

import java.util.Arrays;
import java.util.stream.IntStream;

public class GraphFactory {

    public static Node[] binaryString(int maxThreads, int length) {
        Node[] nodes = IntStream.range(0, length + 1).mapToObj(i -> new Node(2, maxThreads)).toArray(Node[]::new);
        nodes[nodes.length - 1] = new Node(0, maxThreads);
        for (int i = 0; i < nodes.length - 1; i++) {
            nodes[i].addEdge(nodes[i + 1], "0");
            nodes[i].addEdge(nodes[i + 1], "1");
            nodes[i].initChances();
        }
        return nodes;
    }

    public static Node[] snakeInTheBox(int maxThreads, int dimensions) {
        Node[] nodes = IntStream.range(0, 1 << dimensions).mapToObj(i -> new Node(dimensions, maxThreads)).toArray(Node[]::new);
        int[] oneMaskes = new int[dimensions];
        int[] zeroMaskes = new int[dimensions];
        oneMaskes[0] = 1;
        for (int i = 1; i < oneMaskes.length; i++) oneMaskes[i] = oneMaskes[i - 1] << 1;
        for (int i = 0; i < zeroMaskes.length; i++) zeroMaskes[i] = ~oneMaskes[i];
        for (int i = 0; i < nodes.length; i++) {
            Node node = nodes[i];
            for (int j = 0; j < oneMaskes.length; j++)
                node.addEdge(nodes[(((oneMaskes[j] & i) != 0) ? zeroMaskes[j] & i : oneMaskes[j] | i)], 0);
            node.initChances();
        }
        return nodes;
    }

    public static Node[] travelingSalesMan(int maxThreads, double[][] points, boolean addDistanceBias) {
        var nodes = new Node[points.length];
        for(int i = 0; i < nodes.length; i++)
            nodes[i] = new Node(points.length - 1, points[i][0] + ", " + points[i][1], maxThreads);

        double[][] distanceMap = new double[points.length][points.length];
        for (int i = 0; i < points.length; i++)
            for (int j = i + 1; j < points.length; j++)
                distanceMap[i][j] = Math.sqrt(Math.pow(points[i][0] - points[j][0], 2) + Math.pow(points[i][1] - points[j][1], 2));

        for (int i = 0; i < nodes.length; i++) {
            for (int j = i + 1; j < nodes.length; j++) {
                nodes[i].addEdge(nodes[j], distanceMap[i][j]);
                nodes[j].addEdge(nodes[i], distanceMap[i][j]);
            }
        }

        if (addDistanceBias) {
            for (Node node : nodes) {
                double sum = Arrays.stream(node.getCosts()).sum();
                node.initChances(Arrays.stream(node.getCosts()).map(i -> sum - i).map(i -> i <= 0D ? 1D : i).toArray());
            }
        } else for (Node node : nodes)
            node.initChances();
        return nodes;
    }

}
