package ni.antcolony.visitation;

import ni.antcolony.Node;

public class Visitations {

    public static VisitationInterface none(){
        return (id, ant, node, pick) -> {};
    }

    public static VisitationInterface addCurrentNode(){
        return (id, ant, node, pick) -> node.lastUsage = id;
    }

    public static VisitationInterface addCurrentAndEdgeNodes(){
        return (id, ant, node, pick) -> {
            node.lastUsage = id;
            for(Node n : node.getNodes()) n.lastUsage = id;
        };
    }

    public static VisitationInterface addCurrentNodesAfterX(final int x){
        return (id, ant, node, pick) -> {
            if(ant.getInsertionCount() > x)
                node.lastUsage = id;
        };
    }


}
