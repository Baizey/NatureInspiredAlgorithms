package natural.genetic.fitness;

public class Fitness {

    public static FitnessInterface oneMax(){
        return (preCalc, individual) -> individual.getDna().cardinality();
    }

    public static FitnessInterface zeroMax(){
        return (preCalc, individual) -> individual.getLength() - individual.getDna().cardinality();
    }

}
