package natural.GA;

import lsm.helpers.IO.write.text.console.Note;
import natural.AbstractIndividual;
import natural.interfaces.PreCalc;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;

public class PreCalcs {

    public static PreCalc minAndSum() {
        return (individuals, previousData) -> {
            if (previousData == null) previousData = new PreCalcData(2, 0);
            previousData.longs[0] = Arrays.stream(individuals).mapToLong(AbstractIndividual::getFitness).min().orElse(0);
            long absMin = Math.abs(Math.min(0, previousData.longs[0]));
            previousData.longs[1] = Arrays.stream(individuals).mapToLong(i -> i.getFitness() + absMin).sum();
            return previousData;
        };
    }

    public static PreCalc cheapSkipChance(double mutationRate) {
        return (individuals, previousData) -> {
            if (previousData != null) return previousData;
            PreCalcData result = new PreCalcData(30, 30);
            result.doubles[0] = 1D - mutationRate;
            result.longs[0] = 1L;
            for (int i = 1; i < result.longs.length; i++) {
                result.doubles[i] = result.doubles[i - 1] * result.doubles[i - 1];
                result.longs[i] = result.longs[i - 1] << 1;
            }
            return result;
        };
    }

    /**
     * Will only work as long as demanded precision is below n / 2
     * with n = 100,000 and precision = 0.99999999 only needs the first 21
     *
     * @param precision percent precision (0 <= x < 1)
     * @return
     */
    public static PreCalc exactSkipChance(double precision) {
        return (individuals, previousData) -> {
            if (previousData != null) return previousData;
            BigDecimal r = BigDecimal.valueOf(precision);
            int n = individuals[0].getLength();
            int length = n / 2 + 1;
            ArrayList<BigDecimal> permutations = new ArrayList<>() {{
                add(BigDecimal.ONE);
            }};
            ArrayList<BigDecimal> chances = new ArrayList<>();
            BigDecimal
                    a = BigDecimal.ONE.divide(BigDecimal.valueOf(n), 10, RoundingMode.HALF_UP),
                    b = BigDecimal.ONE.subtract(a),
                    c = a.divide(b, 10, RoundingMode.HALF_UP),
                    at = b.pow(n),
                    sumSoFar = BigDecimal.ZERO;
            for (int i = 0; i < length; i++) {
                if (i > 0) permutations.add(
                        permutations.get(i - 1)
                                .multiply(BigDecimal.valueOf((n - i + 1)))
                                .divide(BigDecimal.valueOf(i), 10, RoundingMode.HALF_UP));
                chances.add(permutations.get(i).multiply(at));
                sumSoFar = sumSoFar.add(chances.get(chances.size() - 1));
                Note.writenl(sumSoFar.doubleValue());
                if (sumSoFar.compareTo(r) > 0) break;
                at = at.multiply(c);
            }
            PreCalcData data = new PreCalcData(0, permutations.size());
            for (int i = 0; i < permutations.size(); i++)
                data.doubles[i] = chances.get(i).doubleValue();
            Note.writenl(data.doubles);
            return data;
        };
    }

    /**
     * Pre calculated exactness
     * with n = 100,000
     * and precision = 0.999999999 (99.9999999%)
     * This can be done with a higher n for more accuracy
     * Calculated using {exactSkipChance}
     * This approximates to 1/(e * k!) where k is genes flipped
     * @return
     */
    public static PreCalc exactPrePreCalculatedSkipChance() {
        return (individuals, previousData) -> {
            if (previousData != null) return previousData;
            return new PreCalcData(null, new double[]{
                    0.36787760176657225D,
                    0.36788128054258995D,
                    0.1839406402529009D,
                    0.06131293360323675D,
                    0.015327926831542701D,
                    0.003065493397521317D,
                    5.108951293754449E-4D,
                    7.298136918749129E-5D,
                    9.122123781781635E-6D,
                    1.0134983584243568E-6D,
                    1.0134172776435344E-7D,
                    9.212055173410265E-9D});
        };
    }

    public static PreCalc none() {
        return (individuals, previousData) -> null;
    }

    public static PreCalc get(String crossoverChoice, String selectionChoice) {
        if(selectionChoice.equalsIgnoreCase("stochastic"))
            return minAndSum();
        return none();
    }
}

