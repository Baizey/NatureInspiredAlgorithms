package ni.antcolony;

import ni.AbstractIndividual;

@SuppressWarnings("unused")
public class Ant extends AbstractIndividual {

    private int at = 0;

    public Ant(int maxSolutionLength){
        super(maxSolutionLength);
        this.fitness = 0;
    }

    @Override
    public void reset() {
        this.fitness = 0;
        this.at = 0;
    }
}
