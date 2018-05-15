package natural.factory;

import natural.ACO.Edge;
import natural.ACO.Node;
import natural.interfaces.Bias;

public class GraphFactory {

    private static final int[] bestRoute = new int[]{1, 49, 32, 45, 19, 41, 8, 9, 10, 43, 33, 51, 11, 52, 14, 13, 47, 26, 27, 28, 12, 25, 4, 6, 15, 5, 24, 48, 38, 37, 40, 39, 36, 35, 34, 44, 46, 16, 29, 50, 20, 23, 30, 2, 7, 42, 21, 17, 3, 18, 31, 22,};

    public static Node[] travelingSalesMan(int maxThreads, double[][] points, Bias bias) {
        var nodes = new Node[points.length];
        for(int i = 0; i < nodes.length; i++)
            nodes[i] = new Node(points[i][0] + ", " + points[i][1], maxThreads);

        double[][] distanceMap = new double[points.length][points.length];
        for (int i = 0; i < points.length; i++)
            for (int j = 0; j < points.length; j++)
                distanceMap[i][j] = Math.sqrt(Math.pow(points[i][0] - points[j][0], 2) + Math.pow(points[i][1] - points[j][1], 2));

        for (int i = 0; i < nodes.length; i++) {
            nodes[i].setMaxEdges(points.length - 1);
            for (int j = 0, nextEdge = 0; j < nodes.length; j++) {
                if(i == j) continue;
                nodes[i].setEdge(new Edge(nodes[i], distanceMap[i][j], nodes[j]), nextEdge);
                nextEdge++;
            }
        }

        for(Node node : nodes)
            bias.giveBias(node);

        return nodes;
    }

}
