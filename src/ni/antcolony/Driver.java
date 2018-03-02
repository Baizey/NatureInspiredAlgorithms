package ni.antcolony;

import ni.antcolony.visitation.Visitations;

public class Driver {

    public static void main(String... args) {

        int size = 100000;
        Node start = new Node(2);
        start.addEdge(start, "0");
        start.addEdge(start, "1");
        start.initChances(new double[]{1, 100});

        Colony colony = new Colony(
                100,
                0.01,
                start,
                Visitations.addCurrentNodesAfterX(size - 1),
                (ant, node) -> {
                    int fitness = 0;
                    for(int i = 0; i < ant.getInsertionCount(); i++)
                        if(start.getName(ant.getChoice(i)).charAt(0) == '1')
                            fitness++;
                    ant.setFitness(fitness);
                }
        );
        colony.evolve(size, Integer.MAX_VALUE);

    }

}
