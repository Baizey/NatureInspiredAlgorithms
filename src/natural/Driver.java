package natural;

import natural.benchmark.GraphingData;

@SuppressWarnings("WeakerAccess")
public class Driver {

    public static void main(String[] args) throws Exception {
        String filename = "TSPP";
        //filename += "Avg";
        GraphingData.generate(filename);
        GraphingData.launchDisplay(filename);
    }

}