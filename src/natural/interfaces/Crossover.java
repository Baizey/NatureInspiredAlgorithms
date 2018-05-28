package natural.interfaces;

import natural.genericGA.binaryGA.PreCalcData;
import natural.genericGA.GenericIndividual;

@SuppressWarnings("unused")
public interface Crossover {
    void crossover(PreCalcData preCalc, GenericIndividual male, GenericIndividual female, GenericIndividual baby);
}

