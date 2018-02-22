package ni.genetic.mutations;

        import ni.genetic.Individual;

@SuppressWarnings("unused")
public interface MutationInterface {
    void mutate(int[] preCalc, Individual individual, double mutationRate);
}
