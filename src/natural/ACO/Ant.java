package natural.ACO;

import natural.AbstractIndividual;

import java.util.Arrays;

public class Ant extends AbstractIndividual<Edge[]> {

    protected int insertionPoint = 0;

    public Ant(int estimatedMaxLength) {
        super(estimatedMaxLength);
        this.solution = new Edge[length];
    }

    public void resetInsertion() {
        insertionPoint = 0;
    }

    public void add(Edge edge) {
        if (insertionPoint >= length) {
            length *= 2;
            Edge[] dna = new Edge[length];
            System.arraycopy(this.solution, 0, dna, 0, insertionPoint);
            this.solution = dna;
        }
        solution[insertionPoint++] = edge;
    }

    public void copy(AbstractIndividual individual) {
        Ant other = (Ant) individual;
        fitness = other.fitness;
        insertionPoint = other.insertionPoint;
        if (other.length > length)
            length = other.length;
        solution = Arrays.copyOf(other.solution, length);
    }

    public Edge[] getUsedDna() {
        return Arrays.copyOf(solution, insertionPoint);
    }

    public int getChoiceId(int index) {
        return solution[index].target.getId();
    }

    public Node getFirstNode(){
        return solution[0].source;
    }

    public Node getLastNode(){
        return solution[insertionPoint - 1].target;
    }

    public Node[] getNodes(){
        return Arrays.stream(solution).map(e -> e.source).toArray(Node[]::new);
    }

    public Edge[] getEdges(){
        return Arrays.copyOf(solution, insertionPoint);
    }

    public int getInsertionCount() {
        return insertionPoint;
    }

    public Edge getEdge(int i) {
        return solution[i];
    }

    public void setEdge(int i, Edge edge) {
        solution[i] = edge;
    }

    public Edge getLastEdge(){
        return solution[insertionPoint - 1];
    }

    public void addToInsertionPoint(int i){
        this.insertionPoint += i;
    }

    public Node getLastSource() {
        return solution[insertionPoint - 1].source;
    }
}
