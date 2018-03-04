package natural.antcolony;

import natural.AbstractIndividual;

public class Ant extends AbstractIndividual<int[]> {

    private int insertionPoint = 0;

    public Ant(int estimatedMaxLength){
        super(estimatedMaxLength);
        dna = new int[length];
    }

    public void resetInsertion(){
        insertionPoint = 0;
    }

    public void add(int n){
        if(insertionPoint >= length) {
            length *= 2;
            int[] dna = new int[length];
            System.arraycopy(this.dna, 0, dna, 0, insertionPoint);
            this.dna = dna;
        }
        dna[insertionPoint++] = n;
    }

    public void copyFrom(Ant other) {
        insertionPoint = other.insertionPoint;
        if(insertionPoint > length) {
            length = other.length;
            this.dna = new int[length];
        }
        this.fitness = other.fitness;
        System.arraycopy(other.dna, 0, dna, 0, insertionPoint);
    }

    public int[] getUsedDna(){
        int[] result = new int[insertionPoint];
        System.arraycopy(dna, 0, result, 0, insertionPoint);
        return result;
    }

    public int getChoice(int index){
        return dna[index];
    }

    public int getInsertionCount(){
        return insertionPoint;
    }
}
