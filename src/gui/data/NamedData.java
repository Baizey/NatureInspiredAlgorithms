package gui.data;

public abstract class NamedData {

    public final String abbrivation;
    public final String name;

    public NamedData(String name){
        this.name = name;

        StringBuilder sb = new StringBuilder();
        for(char c : name.toCharArray())
            if(c >= 'A' && c <= 'Z')
                sb.append(c);
        this.abbrivation = sb.toString();
    }
}
