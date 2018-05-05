package natural.ACO;

import natural.interfaces.Bias;

import java.util.Arrays;

public class NodeBias {

    public static Bias linearBias(){
        return node -> {
            double max = Arrays.stream(node.getCosts()).max().orElse(1D);
            double[] chances = new double[node.getCosts().length];
            for(int i = 0; i < node.getCosts().length; i++)
                chances[i] = max - node.getCost(i);
            node.initChances(chances);
        };
    }

    public static Bias polynomialBias(){
        return node -> {
            double max = Arrays.stream(node.getCosts()).max().orElse(1D);
            double[] chances = new double[node.getCosts().length];
            for(int i = 0; i < node.getCosts().length; i++)
                chances[i] = Math.pow(max - node.getCost(i), 2);
            node.initChances(chances);
        };
    }

    public static Bias noBias(){
        return Node::initChances;
    }

}
