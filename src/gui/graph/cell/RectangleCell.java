package gui.graph.cell;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class RectangleCell extends Cell {

    public RectangleCell(String id, double x, double y) {
        super(id, 50, x, y);
        Rectangle view = new Rectangle(size, size);
        view.setStroke(Color.DODGERBLUE);
        view.setFill(Color.DODGERBLUE);
        setView(view);
    }

}