package natural.GA.mutations;

        import natural.GA.Individual;

@SuppressWarnings("unused")
public interface MutationInterface {
    void mutate(long[] preCalc, double mutationRate, Individual individual);
}
