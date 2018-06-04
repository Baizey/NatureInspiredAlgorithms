package natural.interfaces;

import natural.ACO.Ant;

import java.util.HashMap;

public interface PhermonePlacer {

    void alter(HashMap<String, Object> memory, Ant ant);

}
