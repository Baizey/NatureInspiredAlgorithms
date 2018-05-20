package natural.islands;

import lsm.helpers.utils.Wrap;
import natural.ACO.Colony;
import natural.GA.Population;
import natural.interfaces.ConvergenceInterface;

public class Convergence {

    public static ConvergenceInterface keepBestAfterColonyX(int x) {
        Wrap<Integer> counter = new Wrap<>(0);
        return (islands -> {
            Colony[] colonies = (Colony[]) islands;
            counter.set(counter.get() + 1);
            if(counter.get() >= x){
                counter.set(0);
                int best = 0;
                for(int i = 1; i < islands.length; i++)
                    if(colonies[i].getBestFitness() > colonies[best].getBestFitness())
                        best = i;
                for(int i = 0; i < islands.length; i++)
                    if(i != best)
                        colonies[i].copyGraphProgression(colonies[best]);
            }
        });
    }

    public static ConvergenceInterface keepBestAfterPopulationX(int x) {
        Wrap<Integer> counter = new Wrap<>(0);
        return (islands -> {
            Population[] populations = (Population[]) islands;
            counter.set(counter.get() + 1);
            if(counter.get() >= x){
                counter.set(0);
                int best = 0;
                for(int i = 1; i < islands.length; i++)
                    if(populations[i].getBestFitness() > populations[best].getBestFitness())
                        best = i;
                for(int i = 0; i < islands.length; i++)
                    if(i != best)
                        populations[i].copyPopulationDnaFrom(populations[best]);
            }
        });
    }

}
