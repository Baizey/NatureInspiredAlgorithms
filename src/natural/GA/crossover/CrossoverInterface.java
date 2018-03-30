package natural.GA.crossover;

import natural.GA.Individual;
import natural.GA.preCalc.PreCalcData;

@SuppressWarnings("unused")
public interface CrossoverInterface {
    void crossover(PreCalcData preCalc, Individual male, Individual female, Individual baby);
}

