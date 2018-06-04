package natural.ACO;

import natural.interfaces.Visitation;

public class Visitations {

    public static Visitation none(){
        return (memory, id, ant, node, pick, threadId) -> {};
    }

    public static Visitation addCurrentNode(){
        return (memory, id, ant, node, pick, threadId) -> node.lastUsage[threadId] = id;
    }

    public static Visitation addCurrentAndEdgeNodes(){
        return (memory, id, ant, node, pick, threadId) -> {
            node.lastUsage[threadId] = id;
            for(Edge edge : node.getEdges()) edge.target.lastUsage[threadId] = id;
        };
    }

    public static Visitation addCurrentNodesAfterX(final int x){
        return (memory, id, ant, node, pick, threadId) -> {
            if(ant.getInsertionCount() > x)
                node.lastUsage[threadId] = id;
        };
    }


}
