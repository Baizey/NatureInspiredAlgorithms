package natural.interfaces;

import natural.ACO.Ant;
import natural.ACO.Node;

public interface Visitation {

    void handleVisitation(int id, Ant ant, Node at, int pick, int threadId);

}
