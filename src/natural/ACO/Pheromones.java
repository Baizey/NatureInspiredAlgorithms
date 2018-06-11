package natural.ACO;

import natural.interfaces.PheromonePlacer;

import java.util.Arrays;

public class Pheromones {

    public static PheromonePlacer percentChange(double change) {
        return (memory, graph, ant) -> {
            var route = ant.getSolution();
            for (int j = 0; j < ant.getInsertionCount(); j++) {
                var edges = route[j].source.getEdges();
                var taking = 0D;
                for (Edge edge : edges) {
                    var takes = edge.chance * change;
                    edge.chance -= takes;
                    taking += takes;
                }
                route[j].chance += taking;
            }
        };
    }

    public static PheromonePlacer MMAS(double min, double max, double change) {
        return (memory, graph, ant) -> {
            var route = ant.getSolution();
            for (int j = 0; j < ant.getInsertionCount(); j++) {
                var edges = route[j].source.getEdges();
                var taking = 0D;
                for (Edge edge : edges) {
                    if (route[j].target.getId() == edge.target.getId())
                        continue;
                    var takes = Math.min(edge.chance * change, edge.chance - min);
                    if (takes > 0D) {
                        edge.chance -= takes;
                        taking += takes;
                    }
                }
                route[j].source.getEdge(route[j].target).chance = Math.min(max, route[j].chance + taking);

                // Ensure the total sum of edge-chances still equal 1.00 (100%)
                // Because we don't return 'extra' chance taken from edges if the chosen edge goes above max chance
                double sum = Arrays.stream(edges).mapToDouble(e -> e.chance).sum();
                if(sum != 1D)
                    for(var edge : edges)
                        edge.chance /= sum;
            }
        };
    }

    public PheromonePlacer get(String name, double min, double max, double change) {
        return name.equalsIgnoreCase("MMAS") ? MMAS(min, max, change) : percentChange(change);
    }
}
