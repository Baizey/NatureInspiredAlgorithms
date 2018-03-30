package natural.GA;

import natural.AbstractIndividual;

@SuppressWarnings("WeakerAccess")
public class Individual extends AbstractIndividual<Dna> {
    /**
     * Default Individual has all dna set at 0
     * Generated has each gene 50/50 as 1 or 0
     * @param length number of dna in an individual
     * @param generate if we use default individual or random
     */
    Individual(int length, boolean generate) {
        super(length);
        dna = new Dna(length);
        if (generate)
            for (int i = 0; i < length; i++)
                if (Math.random() >= 0.5) dna.set(i, true);
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < length; i++)
            sb.append(dna.get(i) ? '1' : '0');
        return sb.toString();
    }

    public void copyDnaFrom(Individual individual) {
        dna.copyFrom(individual.dna);
    }
}
