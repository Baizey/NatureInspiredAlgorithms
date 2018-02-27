package ni.genetic.fitness;

public class Fitness {

    public static FitnessInterface oneMax(){
        return (preCalc, individual) -> individual.genes.cardinality();
    }

    public static FitnessInterface zeroMax(){
        return (preCalc, individual) -> individual.geneSize - individual.genes.cardinality();
    }

}
