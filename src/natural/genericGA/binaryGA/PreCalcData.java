package natural.genericGA.binaryGA;

/**
 * Flexible (and likely confusing) data container
 * Calculated at the start of every new generation (before it's generated)
 * Available throughout every step, selection, crossover, mutation, and fitness.
 */

@SuppressWarnings("WeakerAccess")
public class PreCalcData {
    public final long[] longs;
    public final double[] doubles;
    public final String[] strings;

    public PreCalcData(){
        this.longs = null;
        this.doubles = null;
        this.strings = null;
    }

    public PreCalcData(int longs, int doubles, int strings){
        this.longs = new long[longs];
        this.doubles = new double[doubles];
        this.strings = new String[strings];
    }

    public PreCalcData(long[] longs, double[] doubles, String[] strings){
        this.longs = longs;
        this.doubles = doubles;
        this.strings = strings;
    }
}
