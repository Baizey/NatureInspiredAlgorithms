package gui;

import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

class UICreate {

    static HBox field(String name, Node field) {
        return new HBox(new Label(name + ": "), field);
    }

    static HBox field(String name, ComboBox<String> box) {
        box.getSelectionModel().selectFirst();
        return field(name, (Node) box);
    }
}
