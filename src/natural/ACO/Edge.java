package natural.ACO;

public class Edge {

    public final String name;
    public final double cost;
    public Node source;
    public Node target;
    public double chance;

    public Edge(Node source, Edge other) {
        this(source, other.name, other.cost, other.chance, null);
    }

    public Edge(Node source, Node target) {
        this(source, target.name, target);
    }

    public Edge(Node source, String name, Node target) {
        this(source, name, 1D, 1D, target);
    }

    public Edge(Node source, double cost, Node target) {
        this(source, cost, 1D, target);
    }

    public Edge(Node source, double cost, double chance, Node target) {
        this(source, target.name, cost, chance, target);
    }

    public Edge(Node source, String name, double cost, double chance, Node target) {
        this.source = source;
        this.target = target;
        this.cost = cost;
        this.name = name;
        this.chance = chance;
    }

}
