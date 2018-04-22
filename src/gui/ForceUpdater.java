package gui;

import javafx.beans.Observable;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.util.Callback;

public class ForceUpdater {
    StringProperty name = new SimpleStringProperty();
    IntegerProperty id = new SimpleIntegerProperty();

    public static Callback<ForceUpdater, Observable[]> extractor() {
        return param -> new Observable[]{param.id, param.name};
    }

    public String toString() {
        return String.format("%s: %s", name.get(), id.get());
    }
}