package ni.antcolony.visitation;

import ni.antcolony.Ant;
import ni.antcolony.Node;

import java.util.HashSet;

public interface VisitationInterface {

    void handleVisitation(HashSet<Node> visited, Ant ant, Node at, int pick);

}
