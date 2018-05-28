package natural.genericGA.binaryGA;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

@SuppressWarnings("WeakerAccess")
public class Mutation {
    private static final Random random = new Random();

    public static natural.interfaces.Mutation alwaysFlipOne() {
        return (preCalc, individual) -> ((BinaryIndividual) individual).getDna().flip(random.nextInt(individual.getLength()));
    }

    public static natural.interfaces.Mutation flipOne(double mutationRate) {
        return (preCalc, individual) -> {
            if (random.nextDouble() < mutationRate) {
                int i = random.nextInt(individual.getLength());
                ((BinaryIndividual) individual).getDna().flip(i);
            }
        };
    }

    public static natural.interfaces.Mutation flipXGenes(int count) {
        return (preCalc, individual) -> {
            int genes = individual.getLength();
            var dna = (Dna) individual.getDna();
            for (int i = 0; i < count; i++)
                dna.flip(random.nextInt(genes));
        };
    }

    private final static double[] flipChances = new double[30];
    static {
        BigDecimal at = BigDecimal.ONE.divide(BigDecimal.valueOf(Math.E), 10, RoundingMode.HALF_UP);
        flipChances[0] = at.doubleValue();
        for(int i = 1; i < flipChances.length; i++)
            flipChances[i] = (at = at.divide(BigDecimal.valueOf(i), 10, RoundingMode.HALF_UP)).doubleValue();
    }

    public static natural.interfaces.Mutation onePlusOne() {
        return (preCalc, individual) -> {
            var roll = random.nextDouble();
            int i;
            for(i = 0; i < flipChances.length; i++)
                if((roll -= flipChances[i]) <= 0) break;
            var dna = (Dna)individual.getDna();
            var length = individual.getLength();
            for(var j = 0; j < i; j++)
                dna.flip(random.nextInt(length));
        };
    }

    public static natural.interfaces.Mutation none() {
        return (preCalc, individual) -> {
        };
    }

    public static natural.interfaces.Mutation get(String mutationChoice) {
        switch (mutationChoice.toLowerCase()){
            case "flip one": return flipXGenes(1);
            case "flip two": return flipXGenes(2);
            case "flip three": return flipXGenes(3);
            case "(1 + 1)": return onePlusOne();
            default: return none();
        }
    }
}

