package natural.interfaces;

import natural.GA.Individual;
import natural.GA.PreCalcData;

@SuppressWarnings("unused")
public interface Crossover {
    void crossover(PreCalcData preCalc, Individual male, Individual female, Individual baby);
}

