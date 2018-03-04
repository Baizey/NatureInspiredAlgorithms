package natural.antcolony.visitation;

import natural.antcolony.Ant;
import natural.antcolony.Node;

public interface VisitationInterface {

    void handleVisitation(int id, Ant ant, Node at, int pick);

}
