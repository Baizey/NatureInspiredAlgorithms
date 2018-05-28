package natural.islands;

import lsm.helpers.utils.Wrap;
import natural.ACO.Colony;
import natural.genericGA.binaryGA.BinaryPopulation;

public class Convergence {

    public static natural.interfaces.Convergence keepBestAfterColonyX(int x) {
        var counter = new Wrap<>(0);
        return (islands -> {
            Colony[] colonies = (Colony[]) islands;
            counter.set(counter.get() + 1);
            if (counter.get() >= x) {
                counter.set(0);
                int best = 0;
                for (int i = 1; i < islands.length; i++)
                    if (colonies[i].getBestFitness() > colonies[best].getBestFitness())
                        best = i;
                for (int i = 0; i < islands.length; i++)
                    if (i != best)
                        colonies[i].copyPopulation(colonies[best]);
            }
        });
    }

    public static natural.interfaces.Convergence keepBestAfterPopulationX(int x) {
        var counter = new Wrap<>(0);
        return (islands -> {
            BinaryPopulation[] populations = (BinaryPopulation[]) islands;
            counter.set(counter.get() + 1);
            if (counter.get() >= x) {
                counter.set(0);
                int best = 0;
                for (int i = 1; i < islands.length; i++)
                    if (populations[i].getBestFitness() > populations[best].getBestFitness())
                        best = i;
                for (int i = 0; i < islands.length; i++)
                    if (i != best)
                        populations[i].copyPopulation(populations[best]);
            }
        });
    }

}
