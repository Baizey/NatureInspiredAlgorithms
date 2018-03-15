package natural;

@SuppressWarnings("ALL")
public abstract class AbstractIndividual <T> {
    private static final long UNSET_FITNESS = Long.MIN_VALUE;
    protected T dna;
    protected long fitness = UNSET_FITNESS;
    protected int length;

    public AbstractIndividual(int length) {
        this.length = length;
    }

    public void setFitness(AbstractIndividual other) {
        fitness = other.fitness;
    }

    public void setFitness(long fitness) {
        this.fitness = fitness;
    }

    public void resetFitness() {
        fitness = UNSET_FITNESS;
    }

    public boolean needsFitnessCalculation() {
        return fitness == UNSET_FITNESS;
    }

    public long getFitness() {
        return fitness;
    }

    public int getLength(){
        return length;
    }

    public T getDna(){
        return dna;
    }

}