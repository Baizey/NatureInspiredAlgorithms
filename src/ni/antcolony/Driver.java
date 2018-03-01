package ni.antcolony;

import lsm.helpers.Time;
import ni.antcolony.visitation.Visitations;

public class Driver {

    public static void main(String... args) {

        int size = 1000;

        Node[] nodes = new Node[size];
        for(int i = 0; i < nodes.length; i++)
            nodes[i] = new Node(2);
        for(int i = 0; i < nodes.length - 1; i++) {
            nodes[i].addEdge(nodes[i + 1], "0");
            nodes[i].addEdge(nodes[i + 1], "1");
            nodes[i].initChances();
        }
        nodes[size - 1].addEdge(nodes[size - 1], "");
        nodes[size - 1].addEdge(nodes[size - 1], "");
        nodes[size - 1].initChances();
        Node start = nodes[0];

        Colony colony = new Colony(size - 1, 10, 0.05, start, Visitations.addCurrentAndEdgeNodesAfterX(size - 1), (ant, node) -> {
            int last = ant.getInsertionCount();
            int fitness = 0;
            for(int i = 0; i < last; i++)
                if(ant.getChoice(i) == 1) fitness++;
            ant.setFitness(fitness);
        });

        Time.takeTime(() -> colony.evolve(10));

    }

}
