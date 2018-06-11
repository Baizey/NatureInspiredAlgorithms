package natural.ACO;

import natural.interfaces.StartingPoint;

import java.util.Random;

public class Starts {

    private static final Random random = new Random();

    public static StartingPoint first() {
        return (memory, nodes) -> nodes[0];
    }

    public static StartingPoint nodeAtIndex(int x) {
        return (memory, nodes) -> nodes[x];
    }

    public static StartingPoint random() {
        return (memory, nodes) -> nodes[random.nextInt(nodes.length)];
    }

}
