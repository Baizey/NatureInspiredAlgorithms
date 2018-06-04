package natural.interfaces;

import natural.ACO.Ant;
import natural.ACO.Node;

import java.util.HashMap;

public interface Visitation {

    void handleVisitation(HashMap<String, Object> memory, long id, Ant ant, Node at, int pick, int threadId);

}
