package natural.interfaces;

        import natural.genericGA.binaryGA.PreCalcData;
import natural.genericGA.GenericIndividual;

@SuppressWarnings("unused")
public interface Mutation {
    void mutate(PreCalcData preCalc, GenericIndividual individual);
}
