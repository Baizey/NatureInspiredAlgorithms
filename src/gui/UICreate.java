package gui;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

class UICreate {
    private static final int
            fieldWidth = 150,
            labelWidth = 115;

    private static Label getLabel(String text) {
        return new Label(text + ": ");
    }

    static HBox field(String name, Node field) {
        var label = getLabel(name);
        label.setMinWidth(labelWidth);
        label.setMaxWidth(labelWidth);
        return new HBox(label, field);
    }

    static HBox field(String name, TextField field) {
        field.setMinWidth(fieldWidth);
        field.setMaxWidth(fieldWidth);
        field.setPrefWidth(fieldWidth);
        return field(name, (Node) field);
    }

    static HBox field(String name, ComboBox field) {
        field.getSelectionModel().selectFirst();
        field.setMinWidth(fieldWidth);
        field.setMaxWidth(fieldWidth);
        field.setPrefWidth(fieldWidth);
        return field(name, (Node) field);
    }

    static HBox field(Button field) {
        field.setMinWidth(fieldWidth + labelWidth);
        field.setMaxWidth(fieldWidth + labelWidth);
        field.setPrefWidth(fieldWidth + labelWidth);
        return new HBox(field);
    }
}
