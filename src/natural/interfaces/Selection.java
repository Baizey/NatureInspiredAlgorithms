package natural.interfaces;

import natural.genericGA.binaryGA.PreCalcData;
import natural.genericGA.GenericIndividual;

public interface Selection {

    GenericIndividual select(PreCalcData preCalc, GenericIndividual[] individuals);

}
