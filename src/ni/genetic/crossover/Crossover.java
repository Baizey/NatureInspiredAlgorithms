package ni.genetic.crossover;

import ni.genetic.Individual;

@SuppressWarnings("unused")
public class Crossover {

    public static CrossoverInterface halfAndHalf(){
        return (preCalc, male, female, baby) -> {
            int geneSize = baby.getLength();
            int split = geneSize / 2;
            baby.getDna().copyFrom(male.getDna(), 0, split);
            baby.getDna().copyFrom(female.getDna(), split, geneSize);
        };
    }

    public static CrossoverInterface halfAndHalfRandom(){
        return (preCalc, male, female, baby) -> {
            int geneSize = baby.getLength();
            int split = geneSize / 2;
            if(Math.random() > 0.5) {
                Individual temp = male;
                male = female;
                female = temp;
            }
            baby.getDna().copyFrom(male.getDna(), 0, split);
            baby.getDna().copyFrom(female.getDna(), split, geneSize);
        };
    }

    public static CrossoverInterface fitnessDeterminedHalfAndHalf(){
        return (preCalc, male, female, baby) -> {
            int maleFitness = male.getFitness(),
                femaleFitness = female.getFitness(),
                totalFitness = maleFitness + femaleFitness;
            int geneSize = baby.getLength();
            int split = (int) (geneSize * ((double)maleFitness / totalFitness));
            baby.getDna().copyFrom(male.getDna(), 0, split);
            baby.getDna().copyFrom(female.getDna(), split, geneSize);
        };
    }

    public static CrossoverInterface none() {
        return (preCalc, male, female, baby) -> baby.getDna().copyFrom(male.getDna());
    }
}
