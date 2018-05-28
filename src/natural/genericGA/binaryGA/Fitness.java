package natural.genericGA.binaryGA;



public class Fitness {

    public static natural.interfaces.Fitness oneMax(){
        return (individual -> individual.setFitness(((BinaryIndividual) individual).getDna().cardinality()));
    }

    public static natural.interfaces.Fitness leadingOnes(){
        return (individual -> individual.setFitness(((BinaryIndividual) individual).getDna().leadingOnes()));
    }

    public static natural.interfaces.Fitness subsetSum(int goal, int[] nums) {
        return individual -> {
            Dna dna = ((BinaryIndividual) individual).getDna();
            int sum = 0;
            for(int i = 0; i < nums.length; i++)
                if(dna.get(i))
                    sum += nums[i];
            individual.setFitness(Integer.MAX_VALUE - Math.abs(sum - goal));
        };
    }
}
