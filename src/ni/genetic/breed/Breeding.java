package ni.genetic.breed;

import ni.genetic.Individual;

@SuppressWarnings("unused")
public class Breeding {

    public static BreedInterface halfAndHalf(){
        return (preCalc, male, female, baby) -> {
            int geneSize = baby.geneSize;
            int split = geneSize / 2;
            baby.genes.copyFrom(male.genes, 0, split);
            baby.genes.copyFrom(female.genes, split, geneSize);
        };
    }

    public static BreedInterface halfAndHalfRandom(){
        return (preCalc, male, female, baby) -> {
            int geneSize = baby.geneSize;
            int split = geneSize / 2;
            if(Math.random() > 0.5) {
                Individual temp = male;
                male = female;
                female = temp;
            }
            baby.genes.copyFrom(male.genes, 0, split);
            baby.genes.copyFrom(female.genes, split, geneSize);
        };
    }

    public static BreedInterface fitnessDeterminedHalfAndHalf(){
        return (preCalc, male, female, baby) -> {
            int maleFitness = male.fitness,
                femaleFitness = female.fitness,
                totalFitness = maleFitness + femaleFitness;
            int geneSize = baby.geneSize;
            int split = (int) (geneSize * ((double)maleFitness / totalFitness));
            baby.genes.copyFrom(male.genes, 0, split);
            baby.genes.copyFrom(female.genes, split, geneSize);
        };
    }

    public static BreedInterface none() {
        return (preCalc, male, female, baby) -> baby.genes.copyFrom(male.genes);
    }
}
