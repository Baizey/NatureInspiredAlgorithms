package natural.interfaces;

import natural.ACO.Node;

import java.util.HashMap;

public interface StartingPoint {
    Node getNode(HashMap<String, Object> memory, Node[] nodes);
}
