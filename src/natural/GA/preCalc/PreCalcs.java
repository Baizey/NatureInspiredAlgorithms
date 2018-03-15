package natural.GA.preCalc;

import natural.AbstractIndividual;

import java.util.Arrays;

public class PreCalcs {

    public static PreCalcInterface minAndSum(){
        return (individuals) -> {
            long[] result = new long[2];
            result[0] = Arrays.stream(individuals).mapToLong(AbstractIndividual::getFitness).min().orElse(0);
            long absMin = Math.abs(Math.min(0, result[0]));
            result[1] = Arrays.stream(individuals).mapToLong(i -> i.getFitness() + absMin).sum();
            return result;
        };
    }

    public static PreCalcInterface none() {
        return (individuals) -> null;
    }
}
