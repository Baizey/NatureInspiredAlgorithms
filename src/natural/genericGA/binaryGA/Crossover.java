package natural.genericGA.binaryGA;

import java.util.Random;

@SuppressWarnings("unused")
public class Crossover {
    private static final Random random = new Random();

    public static natural.interfaces.Crossover halfAndHalf() {
        return (preCalc, male, female, baby) -> {
            int geneSize = baby.getLength();
            int split = geneSize / 2;
            ((BinaryIndividual) baby).getDna().copyFrom(((BinaryIndividual) male).getDna(), 0, split);
            ((BinaryIndividual) baby).getDna().copyFrom(((BinaryIndividual) female).getDna(), split, geneSize);
        };
    }

    public static natural.interfaces.Crossover halfAndHalfRandom() {
        return (preCalc, male, female, baby) -> {
            if (random.nextDouble() >= 0.5) {
                BinaryIndividual temp = (BinaryIndividual) male;
                male = female;
                female = temp;
            }
            int geneSize = baby.getLength();
            int split = geneSize / 2;
            ((BinaryIndividual) baby).getDna().copyFrom(((BinaryIndividual) male).getDna(), 0, split);
            ((BinaryIndividual) baby).getDna().copyFrom(((BinaryIndividual) female).getDna(), split, geneSize);
        };
    }

    public static natural.interfaces.Crossover fitnessDeterminedHalfAndHalf() {
        return (preCalc, male, female, baby) -> {
            double maleFitness = male.getFitness(),
                    femaleFitness = female.getFitness(),
                    totalFitness = maleFitness + femaleFitness;
            int geneSize = baby.getLength();
            int split = (int) (geneSize * (maleFitness / totalFitness));
            ((BinaryIndividual) baby).getDna().copyFrom(((BinaryIndividual) male).getDna(), 0, split);
            ((BinaryIndividual) baby).getDna().copyFrom(((BinaryIndividual) female).getDna(), split, geneSize);
        };
    }

    public static natural.interfaces.Crossover none() {
        return (preCalc, male, female, baby) -> ((BinaryIndividual) baby).getDna().copyFrom(((BinaryIndividual) male).getDna());
    }

    public static natural.interfaces.Crossover get(String crossoverChoice) {
        switch (crossoverChoice.toLowerCase()){
            case "half and half": return halfAndHalf();
            case "half and half random": return halfAndHalfRandom();
            case "fitness determined half and half": return fitnessDeterminedHalfAndHalf();
            default: return none();
        }
    }
}
