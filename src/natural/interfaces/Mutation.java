package natural.interfaces;

        import natural.GA.Individual;
        import natural.GA.PreCalcData;

@SuppressWarnings("unused")
public interface Mutation {
    void mutate(PreCalcData preCalc, Individual individual);
}
