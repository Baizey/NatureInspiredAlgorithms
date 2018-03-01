package ni;

@SuppressWarnings("ALL")
public abstract class AbstractIndividual <T> {
    private static final int UNSET_FITNESS = Integer.MIN_VALUE;
    protected T dna;
    protected int fitness = UNSET_FITNESS;
    protected int length;

    public AbstractIndividual(int length) {
        this.length = length;
    }

    public void setFitness(AbstractIndividual other) {
        fitness = other.fitness;
    }

    public void setFitness(int fitness) {
        this.fitness = fitness;
    }

    public void resetFitness() {
        fitness = UNSET_FITNESS;
    }

    public boolean needsFitnessCalculation() {
        return fitness == UNSET_FITNESS;
    }

    public int getFitness() {
        return fitness;
    }

    public int getLength(){
        return length;
    }

    public T getDna(){
        return dna;
    }

}