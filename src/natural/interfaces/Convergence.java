package natural.interfaces;

import natural.AbstractPopulation;

import java.util.HashMap;

public interface Convergence {

    void converge(HashMap<String, Object> memory, AbstractPopulation[] islands);

}
