package natural.genericGA;

import natural.AbstractIndividual;

public abstract class GenericIndividual<SolutionType> extends AbstractIndividual<SolutionType> {

    public GenericIndividual(int length, boolean generate) {
        super(length);
        generateDna(generate);
    }

    @Override
    public int getLength() { return length; }

    @Override
    public String toString() {
        return String.valueOf(solution);
    }

    public abstract void generateDna(boolean generate);

    public abstract GenericIndividual<SolutionType> clone(boolean generate);
}
