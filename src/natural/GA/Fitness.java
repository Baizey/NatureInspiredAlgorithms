package natural.GA;

import natural.interfaces.GeneticAlgorithmFitness;

public class Fitness {

    public static GeneticAlgorithmFitness oneMax(){
        return (individual) -> individual.setFitness(individual.getDna().cardinality());
    }

    public static GeneticAlgorithmFitness leadingOnes(){
        return (individual -> individual.setFitness(individual.getDna().leadingOnes()));
    }

    public static GeneticAlgorithmFitness subsetSum(int goal, int[] nums) {
        return individual -> {
            Dna dna = individual.getDna();
            int sum = 0;
            for(int i = 0; i < nums.length; i++)
                if(dna.get(i))
                    sum += nums[i];
            individual.setFitness(Integer.MAX_VALUE - Math.abs(sum - goal));
        };
    }
}
