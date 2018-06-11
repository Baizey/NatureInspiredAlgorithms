package natural.interfaces;

import natural.ACO.Ant;
import natural.ACO.Node;

import java.util.HashMap;

public interface PheromonePlacer {

    void alter(HashMap<String, Object> memory, Node[] graph, Ant ant);

}
