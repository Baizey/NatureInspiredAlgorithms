package ni.genetic;

import ni.AbstractIndividual;

import java.util.Arrays;

@SuppressWarnings("WeakerAccess")
public class Individual extends AbstractIndividual<Boolean> {

    /**
     * Default Individual has all genes set at 0
     * Generated has each gene 50/50 as 1 or 0
     * @param geneSize number of genes in an individual
     * @param generate if we use default individual or random
     */
    Individual(int geneSize, boolean generate) {
        super(geneSize);
        if (generate)
            for (int i = 0; i < geneSize; i++)
                if (Math.random() >= 0.5) genes[i] = true;
        else Arrays.fill(genes, false);
    }


    @Override
    public void reset() {
        this.fitness = Integer.MIN_VALUE;
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < geneSize; i++)
            sb.append(genes[i] ? "1" : "0");
        return sb.toString();
    }
}
