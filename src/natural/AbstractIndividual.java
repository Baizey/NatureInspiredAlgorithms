package natural;

public abstract class AbstractIndividual <SolutionType> {
    public static final long UNSET_FITNESS = Long.MIN_VALUE;
    protected SolutionType solution;
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

    public void setFitness(int fitness) {
        this.fitness = fitness;
    }

    public void resetFitness() {
        fitness = UNSET_FITNESS;
    }

    public long getFitness() {
        return fitness;
    }

    public int getLength(){
        return length;
    }

    public SolutionType getSolution(){
        return solution;
    }

    public abstract void copy(AbstractIndividual individual);
}