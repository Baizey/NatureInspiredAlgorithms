package natural;

import natural.benchmark.GraphingData;

@SuppressWarnings("WeakerAccess")
public class Driver {

    public static void main(String[] args) throws Exception {
        String filename = "LO";
        //filename += "Avg";
        //GraphingData.generate(filename, 5, 5);
        GraphingData.display(filename);
    }

}