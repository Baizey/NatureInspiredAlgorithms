package gui.data;

@SuppressWarnings("WeakerAccess")
public class Algorithm extends NamedData {
    public final Problem[] problems;
    public Algorithm(String name, Problem... problems){
        super(name);
        this.problems = problems;
    }

}
