package natural.GA.select;

import natural.GA.Individual;
import natural.GA.preCalc.PreCalcData;

public interface SelectionInterface {

    Individual select(PreCalcData preCalc, Individual[] individuals);

}
