package natural.genericGA;

import natural.AbstractIndividual;

public abstract class GenericIndividual<T> extends AbstractIndividual<T> {

    public GenericIndividual(int length, boolean generate) {
        super(length);
        generateDna(generate);
    }

    @Override
    public int getLength() { return length; }

    @Override
    public String toString() {
        return String.valueOf(dna);
    }

    public abstract void generateDna(boolean generate);

    public abstract GenericIndividual<T> clone(boolean generate);
}
