package natural.interfaces;

import natural.AbstractIndividual;

import java.util.HashMap;

public interface Fitness {

    void calc(HashMap<String, Object> memory, AbstractIndividual individual);

}
