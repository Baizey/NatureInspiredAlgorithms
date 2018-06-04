package natural.genericGA.binaryGA;

import natural.AbstractIndividual;
import natural.genericGA.GenericIndividual;

@SuppressWarnings("WeakerAccess")
public class BinaryIndividual extends GenericIndividual<Dna> {
    /**
     * Default BinaryIndividual has all solution set at 0
     * Generated has each gene 50/50 as 1 or 0
     *
     * @param length   number of solution in an individual
     * @param generate if we use default individual or random
     */
    BinaryIndividual(int length, boolean generate) {
        super(length, generate);
    }

    public void copy(AbstractIndividual individual) {
        solution.copyFrom(((BinaryIndividual) individual).getSolution());
        fitness = individual.getFitness();
    }

    @Override
    public void generateDna(boolean generate) {
        solution = new Dna(length);
        if (generate)
            for (int i = 0; i < length; i++)
                if (Math.random() >= 0.5) solution.set(i, true);
    }

    @Override
    public GenericIndividual<Dna> clone(boolean generate) {
        BinaryIndividual individual = new BinaryIndividual(length, generate);
        if (!generate) individual.copy(this);
        else individual.generateDna(true);
        return individual;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++)
            sb.append(solution.get(i) ? '1' : '0');
        return sb.toString();
    }
}
