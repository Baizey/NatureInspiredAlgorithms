package natural.interfaces;

import natural.genericGA.GenericIndividual;

import java.util.HashMap;

public interface Selector {

    GenericIndividual select(HashMap<String, Object> memory, GenericIndividual[] individuals);

}
