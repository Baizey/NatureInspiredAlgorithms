package gui;

@SuppressWarnings("WeakerAccess")
public class Algorithm {
    public final String abbrivation;
    public final String name;
    public final Problem[] problems;
    public Algorithm(String name, Problem... problems){
        this.name = name;
        this.abbrivation = name.replaceAll(".*?([A-Z])", "$1").replaceAll("([A-Z]+).*", "$1");
        this.problems = problems;
    }

}
