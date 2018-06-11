package natural.interfaces;

import natural.ACO.Ant;

import java.util.HashMap;

public interface Visitation {

    void handleVisitation(HashMap<String, Object> memory, long id, Ant ant, int threadId);

}
