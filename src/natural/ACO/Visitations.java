package natural.ACO;

import natural.interfaces.Visitation;

public class Visitations {

    public static Visitation none(){
        return (memory, id, ant, threadId) -> {};
    }

    public static Visitation addCurrentNode(){
        return (memory, id, ant, threadId) -> ant.getLastSource().lastUsage[threadId] = id;
    }

    public static Visitation addCurrentAndEdgeNodes(){
        return (memory, id, ant, threadId) -> {
            ant.getLastNode().lastUsage[threadId] = id;
            for(Edge edge : ant.getLastSource().getEdges()) edge.target.lastUsage[threadId] = id;
        };
    }

    public static Visitation addCurrentNodesAfterX(final int x){
        return (memory, id, ant, threadId) -> {
            if(ant.getInsertionCount() > x)
                ant.getLastNode().lastUsage[threadId] = id;
        };
    }


}
