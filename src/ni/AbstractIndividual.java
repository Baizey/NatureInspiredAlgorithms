package ni;

@SuppressWarnings("ALL")
public abstract class AbstractIndividual <T> {

    public final int lastIndex;
    public final int geneSize;
    public int fitness = -1;
    public final T[] genes;

    /**
     * Default Individual has all genes set at 0
     * Generated has each gene 50/50 as 1 or 0
     * @param geneSize number of genes in an individual
     */
    public AbstractIndividual(int geneSize) {
        if(geneSize <= 0) throw new IllegalArgumentException("Needs more than 0 genes");
        this.geneSize = geneSize;
        this.lastIndex = geneSize - 1;
        genes = (T[]) new Object[geneSize];
    }

    public abstract void reset();

    public String toString(){
        return String.valueOf(genes);
    }
}
