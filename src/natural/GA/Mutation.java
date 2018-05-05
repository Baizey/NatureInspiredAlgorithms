package natural.GA;

import java.util.Random;

@SuppressWarnings("unused")
public class Mutation {
    private static final Random random = new Random();

    public static natural.interfaces.Mutation alwaysFlipOne() {
        return (preCalc, individual) -> individual.getDna().flip(random.nextInt(individual.getLength()));
    }

    public static natural.interfaces.Mutation flipOne(double mutationRate) {
        return (preCalc, individual) -> {
            if (random.nextDouble() < mutationRate) {
                int i = random.nextInt(individual.getLength());
                individual.getDna().flip(i);
            }
        };
    }

    public static natural.interfaces.Mutation flipXGenes(int count) {
        return (preCalc, individual) -> {
            int genes = individual.getLength();
            Dna dna = individual.getDna();
            for (int i = 0; i < count; i++)
                dna.flip(random.nextInt(genes));
        };
    }

    /**
     * For each gene flipRandom if it should be flipped
     * Utilizes 'split-merge' tactic
     * Assuming we have 16 genes:
     * check if we roll lower than (1 - 1/16)^16
     * If we do, no genes needs to flip
     * Otherwise do this for (1- 1/16)^8 for 0..8 and 9..16
     * If both rolls succeed and no change is made, change a random gene in 0..16 since our (1 - 1/16)^16 dictated that one of the rolls should have failed
     * else split down to 4, 2 and at last 1.
     *
     * @return individual mutated
     */
    public static natural.interfaces.Mutation flipRandomExact() {
        return (preCalc, individual) -> {
            double[] chances = preCalc.doubles;
            double r = random.nextDouble() - chances[0];
            int i;
            for (i = 0; i < chances.length && r >= 0; i++)
                r -= chances[i];
            Dna dna = individual.getDna();
            int length = individual.getLength();
            while(i-- > 0)
                dna.flip(random.nextInt(length));
        };
    }

    private static void flipRandomExact(int toFlip, Dna dna, int at, int cover) {
        if (toFlip == 1) dna.flip(random.nextInt(at + cover));
        else if (toFlip > 1) {
            flipRandomExact(toFlip >> 1, dna, at, cover >> 1);
            flipRandomExact(toFlip >> 1 + (toFlip & 1), dna, at + (cover >> 1), (cover & 1) + cover >> 1);
        }
    }

    public static natural.interfaces.Mutation flipRandomCheap(double mutationRate) {
        return (preCalc, individual) -> {
            int genes = individual.getLength();
            Dna dna = individual.getDna();
            double[] chances = preCalc.doubles;
            long[] chanceSkips = preCalc.longs;
            int length = chanceSkips.length;
            int i = length - 1;
            int left = genes;
            int at = 0;
            while (left > 0) {
                while (chanceSkips[i] > left) i--;
                flipRandomPart(preCalc, dna, at, i);
                at += chanceSkips[i];
                left -= chanceSkips[i];
            }
        };
    }

    private static boolean flipRandomPart(PreCalcData data, Dna dna, int at, int dataAt) {
        if (random.nextDouble() < data.doubles[dataAt]) return false;
        if (dataAt == 0) dna.flip(at);
        else if (!(flipRandomPart(data, dna, at, dataAt - 1) || flipRandomPart(data, dna, (int) (at + data.longs[dataAt - 1]), dataAt - 1))) {
            dna.flip(at + random.nextInt((int) data.longs[dataAt]));
            if (random.nextDouble() < 0.1) dna.flip(at + random.nextInt((int) data.longs[dataAt]));
        }
        return true;
    }

    public static natural.interfaces.Mutation none() {
        return (preCalc, individual) -> {
        };
    }

    public static natural.interfaces.Mutation get(String mutationChoice) {
        switch (mutationChoice.toLowerCase()){
            case "flip one": return flipXGenes(1);
            case "flip two": return flipXGenes(1);
            case "flip three": return flipXGenes(1);
            case "(1 + 1)": return flipRandomExact();
            default: return none();
        }
    }
}

