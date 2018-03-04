package natural.genetic.preCalc;

import natural.AbstractIndividual;

import java.util.Arrays;

public class PreCalcs {

    public static PreCalcInterface minAndSum(){
        return (individuals) -> {
            double[] result = new double[2];
            result[0] = Arrays.stream(individuals).mapToDouble(AbstractIndividual::getFitness).min().orElse(0);
            double absMin = Math.abs(Math.min(0, result[0]));
            result[1] = Arrays.stream(individuals).mapToDouble(i -> i.getFitness() + absMin).sum();
            return result;
        };
    }

    public static PreCalcInterface none() {
        return (individuals) -> null;
    }
}
