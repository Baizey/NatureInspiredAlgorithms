package natural.ACO;

import natural.interfaces.StartingPoint;

import java.util.Random;

public class Starts {

    private static final Random random = new Random();

    public static StartingPoint first(){
        return nodes -> nodes[0];
    }

    public static StartingPoint random(){
        return nodes -> nodes[random.nextInt(nodes.length)];
    }

}
