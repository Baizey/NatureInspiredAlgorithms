package ni.genetic;

@SuppressWarnings("WeakerAccess")
public class Individual{

    public final int lastIndex;
    public final int geneSize;
    public int fitness = -1;
    public Gene genes;

    /**
     * Default Individual has all genes set at 0
     * Generated has each gene 50/50 as 1 or 0
     * @param geneSize number of genes in an individual
     * @param generate if we use default individual or random
     */
    Individual(int geneSize, boolean generate) {
        this.geneSize = geneSize;
        this.lastIndex = geneSize - 1;
        genes = new Gene(geneSize);
        if (generate)
            for (int i = 0; i < geneSize; i++)
                if (Math.random() >= 0.5) genes.set(i, true);
    }

    public void reset() {
        this.fitness = Integer.MIN_VALUE;
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < geneSize; i++)
            sb.append(genes.get(i) ? '1' : '0');
        return sb.toString();
    }
}
