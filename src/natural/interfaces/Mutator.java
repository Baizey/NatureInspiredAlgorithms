package natural.interfaces;

import natural.AbstractIndividual;

import java.util.HashMap;

public interface Mutator {
    void mutate(HashMap<String, Object> memory, AbstractIndividual individual);
}
