package natural.ACO.visitation;

import natural.ACO.Node;

public class Visitations {

    public static VisitationInterface none(){
        return (id, ant, node, pick, threadId) -> {};
    }

    public static VisitationInterface addCurrentNode(){
        return (id, ant, node, pick, threadId) -> node.lastUsage[threadId] = id;
    }

    public static VisitationInterface addCurrentAndEdgeNodes(){
        return (id, ant, node, pick, threadId) -> {
            node.lastUsage[threadId] = id;
            for(Node n : node.getEdges()) n.lastUsage[threadId] = id;
        };
    }

    public static VisitationInterface addCurrentNodesAfterX(final int x){
        return (id, ant, node, pick, threadId) -> {
            if(ant.getInsertionCount() > x)
                node.lastUsage[threadId] = id;
        };
    }


}
