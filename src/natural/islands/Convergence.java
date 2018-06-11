package natural.islands;

import lsm.helpers.utils.Wrap;

public class Convergence {

    public static natural.interfaces.Convergence keepBestAfterX(int x) {
        var counter = new Wrap<>(0);
        return (memory, islands) -> {
            counter.set(counter.get() + 1);
            if (counter.get() >= x) {
                counter.set(0);
                int best = 0;
                for (int i = 1; i < islands.length; i++)
                    if (islands[i].getBestFitness() > islands[best].getBestFitness())
                        best = i;
                for (int i = 0; i < islands.length; i++)
                    if (i != best)
                        islands[i].copyPopulation(islands[best]);
            }
        };
    }

}
