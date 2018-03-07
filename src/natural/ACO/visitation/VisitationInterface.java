package natural.ACO.visitation;

import natural.ACO.Ant;
import natural.ACO.Node;

public interface VisitationInterface {

    void handleVisitation(int id, Ant ant, Node at, int pick);

}
