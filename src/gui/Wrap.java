package gui;

/**
 * Variable wrapper
 * Allows non-final variables to be perceived as final, gotta love bending the rules
 *
 * @param <Var> variable type... as it may suggest
 */
@SuppressWarnings({"WeakerAccess", "UnusedReturnValue"})
public class Wrap<Var> {
    public Var value;

    public Wrap(Var value) {
        set(value);
    }

    public void set(Var value) {
        this.value = value;
    }

    public Var get() {
        return value;
    }

    public boolean isSame(Var value) {
        if (this.value == null)
            return value == null;
        return this.value.equals(value);
    }

    public String toString() {
        return String.valueOf(value);
    }
}
