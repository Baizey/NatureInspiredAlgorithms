package ni.genetic.select;

import ni.genetic.Individual;

public interface SelectionInterface {

    Individual select(int[] preCalc, Individual[] individuals);

}
