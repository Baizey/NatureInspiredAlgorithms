package natural.genericGA.binaryGA;

import natural.AbstractIndividual;
import natural.interfaces.PreCalc;

import java.util.Arrays;

public class PreCalcs {

    public static PreCalc minAndSum() {
        return (individuals, previousData) -> {
            if (previousData == null) previousData = new PreCalcData(2, 0, 0);
            previousData.longs[0] = Arrays.stream(individuals).mapToLong(AbstractIndividual::getFitness).min().orElse(0);
            long absMin = Math.abs(Math.min(0, previousData.longs[0]));
            previousData.longs[1] = Arrays.stream(individuals).mapToLong(i -> i.getFitness() + absMin).sum();
            return previousData;
        };
    }

    public static PreCalc none() {
        return (individuals, previousData) -> null;
    }

    public static PreCalc get(String crossoverChoice, String selectionChoice, String mutationChoice) {
        return selectionChoice.equalsIgnoreCase("stochastic") ? minAndSum() : none();
    }
}

