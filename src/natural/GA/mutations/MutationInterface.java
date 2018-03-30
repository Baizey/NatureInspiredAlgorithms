package natural.GA.mutations;

        import natural.GA.Individual;
        import natural.GA.preCalc.PreCalcData;

@SuppressWarnings("unused")
public interface MutationInterface {
    void mutate(PreCalcData preCalc, Individual individual);
}
