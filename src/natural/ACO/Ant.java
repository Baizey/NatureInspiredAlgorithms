package natural.ACO;

import natural.AbstractIndividual;

import java.util.Arrays;

public class Ant extends AbstractIndividual<Edge[]> {

    private int insertionPoint = 0;

    public Ant(int estimatedMaxLength) {
        super(estimatedMaxLength);
        this.dna = new Edge[length];
    }

    public void resetInsertion() {
        insertionPoint = 0;
    }

    public void add(Edge edge) {
        if (insertionPoint >= length) {
            length *= 2;
            Edge[] dna = new Edge[length];
            System.arraycopy(this.dna, 0, dna, 0, insertionPoint);
            this.dna = dna;
        }
        dna[insertionPoint++] = edge;
    }

    public void copyFrom(Ant other) {
        fitness = other.fitness;
        insertionPoint = other.insertionPoint;
        if (other.length > length)
            length = other.length;
        dna = Arrays.copyOf(other.dna, length);
    }

    public Edge[] getUsedDna() {
        return Arrays.copyOf(dna, insertionPoint);
    }

    public int getChoiceId(int index) {
        return dna[index].target.getId();
    }

    public Node getFirstNode(){
        return dna[0].source;
    }

    public Node getLastNode(){
        return dna[insertionPoint - 1].target;
    }

    public Node[] getNodes(){
        return Arrays.stream(dna).map(e -> e.source).toArray(Node[]::new);
    }

    public Edge[] getEdges(){
        return Arrays.copyOf(dna, insertionPoint);
    }

    public int getInsertionCount() {
        return insertionPoint;
    }

    public Edge getEdge(int i) {
        return dna[i];
    }

    public void setEdge(int i, Edge edge) {
        dna[i] = edge;
    }
}
