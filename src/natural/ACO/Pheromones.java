package natural.ACO;

import natural.interfaces.PhermonePlacer;

public class Pheromones {

    public static PhermonePlacer percentChange(double change) {
        return ant -> {
            Edge[] edges = ant.getDna();
            for (int i = 0; i < ant.getInsertionCount(); i++)
                edges[i].source.movePercentageTo(change, edges[i].target.getId());
        };
    }

}
