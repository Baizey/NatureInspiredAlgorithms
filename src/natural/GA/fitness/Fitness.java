package natural.GA.fitness;

public class Fitness {

    public static FitnessInterface oneMax(){
        return (individual) -> individual.setFitness(individual.getDna().cardinality());
    }

    public static FitnessInterface zeroMax(){
        return (individual) -> individual.setFitness(individual.getLength() - individual.getDna().cardinality());
    }

}
