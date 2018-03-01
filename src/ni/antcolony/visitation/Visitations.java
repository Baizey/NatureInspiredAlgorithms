package ni.antcolony.visitation;

import java.util.Arrays;

public class Visitations {

    public static VisitationInterface none(){
        return (visited, ant, node, pick) -> {};
    }

    public static VisitationInterface addCurrentNode(){
        return (visited, ant, node, pick) -> visited.add(node);
    }

    public static VisitationInterface addCurrentAndEdgeNodes(){
        return (visited, ant, node, pick) -> {
            visited.add(node);
            visited.addAll(Arrays.asList(node.getNodes()));
        };
    }

    public static VisitationInterface addCurrentAndEdgeNodesAfterX(final int x){
        return (visited, ant, node, pick) -> {
            if(ant.getInsertionCount() > x)
                visited.add(node);
        };
    }


}
