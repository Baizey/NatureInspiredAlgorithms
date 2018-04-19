package gui.graph.cell;

import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

public class TriangleCell extends Cell {

    public TriangleCell(String id, double x, double y) {
        super(id, x, y, 50);
        Polygon view = new Polygon(size / 2, 0, size, size, 0, size);

        view.setStroke(Color.RED);
        view.setFill(Color.RED);

        setView(view);

    }

}