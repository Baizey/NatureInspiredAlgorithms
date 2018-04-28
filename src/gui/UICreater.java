package gui;

import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

public class UICreater {

    public static HBox numericalField(String name, NumberTextField field) {
        return new HBox(new Label(name + ": "), field);
    }


    public static HBox numericalField(String name, double start, double min, double max) {
        return new HBox(new Label(name + ": "), new NumberTextField(start, min, max));
    }

    public static HBox numericalField(String name, int start, int min, int max) {
        return new HBox(new Label(name + ": "), new NumberTextField(start, min, max));
    }

    public static HBox dropdownMenu(String name, ComboBox<String> box) {
        box.getSelectionModel().selectFirst();
        return new HBox(new Label(name + ": "), box);
    }

    public static HBox dropdownMenu(String name, String... dropdown) {
        var drop = new ComboBox<>(FXCollections.observableArrayList(dropdown));
        drop.getSelectionModel().selectFirst();
        return new HBox(new Label(name + ": "), drop);
    }


    public static Node textField(String name, TextField numArray) {
        return new HBox(new Label(name + ": "), numArray);
    }
}
