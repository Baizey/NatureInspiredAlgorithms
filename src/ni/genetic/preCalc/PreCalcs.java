package ni.genetic.preCalc;

import ni.AbstractIndividual;

import java.util.Arrays;

public class PreCalcs {

    public static PreCalcInterface minAndSum(){
        return (individuals) -> {
            int[] result = new int[2];
            result[0] = Arrays.stream(individuals).mapToInt(AbstractIndividual::getFitness).min().orElse(0);
            int absMin = Math.abs(Math.min(0, result[0]));
            result[1] = Arrays.stream(individuals).mapToInt(i -> i.getFitness() + absMin).sum();
            return result;
        };
    }

    public static PreCalcInterface none() {
        return (individuals) -> null;
    }
}
