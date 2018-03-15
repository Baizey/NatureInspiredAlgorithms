package natural.GA.select;

import natural.GA.Individual;

public interface SelectionInterface {

    Individual select(long[] preCalc, Individual[] individuals);

}
