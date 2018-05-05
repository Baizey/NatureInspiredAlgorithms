package natural.interfaces;

import natural.ACO.Ant;
import natural.ACO.Node;

public interface AntColonyFitness {

    void calc(Ant ant, Node start);

}
