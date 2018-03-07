package natural.GA.select;

import natural.GA.Individual;

public interface SelectionInterface {

    Individual select(int[] preCalc, Individual[] individuals);

}
