package ni.genetic.fitness;

public class Fitness {

    public static FitnessInterface oneMax(){
        return (preCalc, individual) -> {
            Boolean[] genes = individual.genes;
            int fitness = 0;
            for(int i = 0; i < genes.length; i++)
                if(genes[i]) fitness++;
            individual.fitness = fitness;
            return fitness;
        };
    }

    public static FitnessInterface zeroMax(){
        return (preCalc, individual) -> {
            Boolean[] genes = individual.genes;
            int fitness = 0;
            for(int i = 0; i < genes.length; i++)
                if(!genes[i]) fitness++;
            individual.fitness = fitness;
            return fitness;
        };
    }

}
