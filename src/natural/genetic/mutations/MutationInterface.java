package natural.genetic.mutations;

        import natural.genetic.Individual;

@SuppressWarnings("unused")
public interface MutationInterface {
    void mutate(double[] preCalc, Individual individual, double mutationRate);
}
