package natural.genetic.select;

import natural.genetic.Individual;

public interface SelectionInterface {

    Individual select(double[] preCalc, Individual[] individuals);

}
