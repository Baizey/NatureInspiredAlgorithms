package natural;

import natural.benchmark.GraphingData;

@SuppressWarnings("WeakerAccess")
public class Driver {

    public static void main(String[] args) throws Exception {
        String first =  "No Parallel";
        String second = "Use Parallel";
        String filename = "TSPC";
        //filename += "random2";
        filename += "Avg";
        //GraphingData.generate(first, 5, 5);
        //GraphingData.generate(second, 5, 5);
        GraphingData.launchDisplay(new String[]{first, second});
        //GraphingData.launchDisplay(new String[]{filename});
    }
}