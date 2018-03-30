package natural.GA.preCalc;

/**
 * Flexible (and likely confusing) data container
 * Calculated at the start of every new generation (before it's generated)
 * Available throughout every step, selection, crossover, mutation, and fitness.
 */

public class PreCalcData {
    public final long[] longs;
    public final double[] doubles;
    public PreCalcData(int longs, int doubles){
        this.longs = new long[longs];
        this.doubles = new double[doubles];
    }
}
