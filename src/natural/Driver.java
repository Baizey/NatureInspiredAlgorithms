package natural;

import natural.benchmark.GraphingData;

@SuppressWarnings("WeakerAccess")
public class Driver {

    public static void main(String[] args) throws Exception {
        String filename = "LO";
        filename += "random";
        //filename += "Avg";
        GraphingData.generate(filename, 10, 1);
        GraphingData.launchDisplay(filename);
    }
}