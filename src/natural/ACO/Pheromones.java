package natural.ACO;

import natural.interfaces.PhermonePlacer;

public class Pheromones {

    public static PhermonePlacer percentChange(double change) {
        return ant -> {
            var route = ant.getDna();
            for (int j = 0; j < ant.getInsertionCount(); j++) {
                var edges = route[j].source.getEdges();
                var taking = 0D;
                for (Edge edge : edges) {
                    if (route[j].target.getId() == edge.target.getId())
                        continue;
                    var takes = edge.chance * change;
                    edge.chance -= takes;
                    taking += takes;
                }
                route[j].source.getEdge(route[j].target).chance += taking;
            }
        };
    }

}
