package natural.ACO;

import lsm.helpers.IO.write.text.console.Note;
import natural.interfaces.Mutator;

import java.util.Random;

public class AntColonyMutations {
    private static final Random random = new Random();

    public static Mutator twoOptCircle() {
        return (memory, individual) -> {
            Ant ant = (Ant) individual;
            ant.add(ant.getLastNode().getEdge(ant.getFirstNode()));
            twoOpt(ant);
        };
    }

    public static Mutator twoOpt() {
        return ((memory, individual) -> twoOpt((Ant) individual));
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
    }

    public static Mutator noneCircle() {
        return (memory, individual) -> {
            Ant ant = (Ant) individual;
            ant.add(ant.getLastNode().getEdge(ant.getFirstNode()));
        };
    }

    public static Mutator none() {
        return (memory, ant) -> {
        };
    }

    public static Mutator get(String name, boolean useCircle) {
        switch (name.toLowerCase()) {
            case "2-opt":
                if (useCircle) return twoOptCircle();
                return twoOpt();
            default:
                if (useCircle) return noneCircle();
                return none();
        }
    }

}
