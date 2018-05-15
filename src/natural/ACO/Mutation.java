package natural.ACO;

import natural.interfaces.AntMutation;

import java.util.Random;

public class Mutation {
    private static final Random random = new Random();

    private static void reverseNodes(Node[] nodes, int start, int end) {
        int half = (end - start) / 2;
        for (int i = 0; i < half; i++) {
            Node t = nodes[i];
            nodes[start + i] = nodes[end - i];
            nodes[end - i] = t;
        }

    }

    public static AntMutation twoOptCircle() {
        return ant -> {
            ant.add(ant.getLastNode().getEdge(ant.getFirstNode()));
            twoOpt(ant);
        };
    }

    public static AntMutation twoOpt() {
        return Mutation::twoOpt;
    }

    private static void twoOpt(Ant ant) {
        // Pick 2 random edges
        int length = ant.getInsertionCount(),
                p1 = random.nextInt(length),
                p2 = random.nextInt(length);
        if (Math.abs(p1 - p2) <= 1) return;

        // Check if it's worth changing them
        Edge a = ant.getEdge(p1),
                b = ant.getEdge(p2),
                c = a.source.getEdge(b.source),
                d = a.target.getEdge(b.target);
        double change = c.cost + d.cost - a.cost - b.cost;
        if (change >= 0) return;

        // change them
        ant.setEdge(p1, c);
        ant.setEdge(p2, d);

        // Reverse half the path to make it function
        for (int i = (p1 + 1 == length ? 0 : p1 + 1); i != p2; i = (++i == length ? 0 : i))
            ant.setEdge(i, ant.getEdge(i).target.getEdge(ant.getEdge(i).source));
    }

    public static AntMutation none() {
        return ant -> {
        };
    }

    public static AntMutation get(String name, boolean useCircle) {
        switch (name.toLowerCase()) {
            case "2-opt":
                if (useCircle) return twoOptCircle();
                return twoOpt();
            default:
                return none();
        }
    }

}
