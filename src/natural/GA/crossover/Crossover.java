package natural.GA.crossover;

import natural.GA.Individual;

import java.util.Random;

@SuppressWarnings("unused")
public class Crossover {
    private static final Random random = new Random();

    public static CrossoverInterface halfAndHalf() {
        return (preCalc, male, female, baby) -> {
            int geneSize = baby.getLength();
            int split = geneSize / 2;
            baby.getDna().copyFrom(male.getDna(), 0, split);
            baby.getDna().copyFrom(female.getDna(), split, geneSize);
        };
    }

    public static CrossoverInterface halfAndHalfRandom() {
        return (preCalc, male, female, baby) -> {
            if (random.nextDouble() >= 0.5) {
                Individual temp = male;
                male = female;
                female = temp;
            }
            int geneSize = baby.getLength();
            int split = geneSize / 2;
            baby.getDna().copyFrom(male.getDna(), 0, split);
            baby.getDna().copyFrom(female.getDna(), split, geneSize);
        };
    }

    public static CrossoverInterface fitnessDeterminedHalfAndHalf() {
        return (preCalc, male, female, baby) -> {
            double maleFitness = male.getFitness(),
                    femaleFitness = female.getFitness(),
                    totalFitness = maleFitness + femaleFitness;
            int geneSize = baby.getLength();
            int split = (int) (geneSize * (maleFitness / totalFitness));
            baby.getDna().copyFrom(male.getDna(), 0, split);
            baby.getDna().copyFrom(female.getDna(), split, geneSize);
        };
    }

    public static CrossoverInterface none() {
        return (preCalc, male, female, baby) -> baby.getDna().copyFrom(male.getDna());
    }

    public static CrossoverInterface get(String crossoverChoice) {
        switch (crossoverChoice.toLowerCase()){
            case "half and half": return halfAndHalf();
            case "half and half random": return halfAndHalfRandom();
            case "fitness determined half and half": return fitnessDeterminedHalfAndHalf();
            default: return none();
        }
    }
}
