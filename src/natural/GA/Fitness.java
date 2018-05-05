package natural.GA;

import natural.interfaces.GeneticAlgorithmFitness;

public class Fitness {

    public static GeneticAlgorithmFitness oneMax(){
        return (individual) -> individual.setFitness(individual.getDna().cardinality());
    }

    public static GeneticAlgorithmFitness leadingOnes(){
        return (individual -> individual.setFitness(individual.getDna().leadingOnes()));
    }

    public static GeneticAlgorithmFitness zeroMax(){
        return (individual) -> individual.setFitness(individual.getLength() - individual.getDna().cardinality());
    }

    public static GeneticAlgorithmFitness subsetSum(int goal, int... nums) {
        return individual -> {
            int sum = 0;
            for(int i = 0; i < nums.length; i++)
                if(individual.getDna().get(i))
                    sum += nums[i];
            individual.setFitness(Integer.MAX_VALUE - Math.abs(sum - goal));
        };
    }
}
