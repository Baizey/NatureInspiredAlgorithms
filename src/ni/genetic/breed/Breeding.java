package ni.genetic.breed;

@SuppressWarnings("unused")
public class Breeding {

    public static BreedInterface halfAndHalf(){
        return (preCalc, male, female, baby) -> {
            int geneSize = baby.geneSize;
            int halfGeneSize = geneSize / 2;
            System.arraycopy(male.genes, 0, baby.genes, 0, halfGeneSize);
            System.arraycopy(female.genes, halfGeneSize, baby.genes, halfGeneSize, halfGeneSize);
        };
    }

    public static BreedInterface halfAndHalfRandom(){
        return (preCalc, male, female, baby) -> {
            int split = baby.geneSize / 2;
            if(Math.random() > 0.5) {
                System.arraycopy(male.genes, 0, baby.genes, 0, split);
                System.arraycopy(female.genes, split, baby.genes, split, split);
            } else {
                System.arraycopy(female.genes, 0, baby.genes, 0, split);
                System.arraycopy(male.genes, split, baby.genes, split, split);
            }
        };
    }

    public static BreedInterface fitnessDeterminedHalfAndHalf(){
        return (preCalc, male, female, baby) -> {
            int maleFitness = male.fitness,
                femaleFitness = female.fitness,
                totalFitness = maleFitness + femaleFitness;
            int split = (int) (baby.geneSize * ((double)maleFitness / totalFitness));
            System.arraycopy(male.genes, 0, baby.genes, 0, split);
            System.arraycopy(female.genes, split, baby.genes, split, split);
        };
    }

    public static BreedInterface none() {
        return (preCalc, male, female, baby) -> System.arraycopy(male.genes, 0, baby.genes, 0, baby.geneSize);
    }
}
