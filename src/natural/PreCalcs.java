package natural;

import natural.interfaces.PreCalc;

import java.util.Arrays;

public class PreCalcs {

    public static PreCalc minAndSum() {
        return (data, previousData) -> {
            var individuals = (AbstractIndividual[]) data;
            previousData.put("min", Arrays.stream(individuals).mapToLong(AbstractIndividual::getFitness).min().orElse(0L));
            long absMin = Math.abs(Math.min(0L, (Long) previousData.getOrDefault("min", 0L)));
            previousData.put("sum", Arrays.stream(individuals).mapToLong(i -> i.getFitness() + absMin).sum());
            return previousData;
        };
    }

    public static <T> PreCalc none() {
        return (PreCalc<T>) (information, previousData) -> previousData;
    }

    public static PreCalc get(String crossoverChoice, String selectionChoice, String mutationChoice) {
        return selectionChoice.equalsIgnoreCase("stochastic") ? minAndSum() : none();
    }

}

