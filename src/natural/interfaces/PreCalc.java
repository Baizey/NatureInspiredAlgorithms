package natural.interfaces;

import natural.genericGA.binaryGA.PreCalcData;
import natural.genericGA.GenericIndividual;

/**
 * Used to handle calculations that are not done for each individual
 * Can handle anything from one-time initial calculation to once for every generation
 * Input will always be:
 * BinaryIndividual[] population: current generations population
 * PreCalcData previousData: pre calculated data from previous generation (or null if first generation)
 */
public interface PreCalc {
    PreCalcData calc(GenericIndividual[] population, PreCalcData previousData);
}
