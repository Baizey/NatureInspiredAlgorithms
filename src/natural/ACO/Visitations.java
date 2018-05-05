package natural.ACO;

import natural.interfaces.Visitation;

public class Visitations {

    public static Visitation none(){
        return (id, ant, node, pick, threadId) -> {};
    }

    public static Visitation addCurrentNode(){
        return (id, ant, node, pick, threadId) -> node.lastUsage[threadId] = id;
    }

    public static Visitation addCurrentAndEdgeNodes(){
        return (id, ant, node, pick, threadId) -> {
            node.lastUsage[threadId] = id;
            for(Node n : node.getEdges()) n.lastUsage[threadId] = id;
        };
    }

    public static Visitation addCurrentNodesAfterX(final int x){
        return (id, ant, node, pick, threadId) -> {
            if(ant.getInsertionCount() > x)
                node.lastUsage[threadId] = id;
        };
    }


}
