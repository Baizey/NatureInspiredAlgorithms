package natural.interfaces;

import natural.GA.Individual;
import natural.GA.PreCalcData;

public interface Selection {

    Individual select(PreCalcData preCalc, Individual[] individuals);

}
