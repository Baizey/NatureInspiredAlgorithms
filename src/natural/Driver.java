package natural;

import natural.benchmark.GraphingData;

public class Driver {

    public static void main(String[] args) throws Exception {
        String first =  "No Parallel";
        String second = "Use Parallel";
        String filename = "SS";
        //filename += "random2";
        filename += "Avg";
        //GraphingData.generate(filename, 50, 50);
        //GraphingData.generate(second, 5, 5);
        //GraphingData.launchDisplay(new String[]{first, second});
        GraphingData.launchDisplay(new String[]{filename});
    }
}