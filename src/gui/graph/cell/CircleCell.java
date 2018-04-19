package gui.graph.cell;


import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class CircleCell extends Cell {

    public CircleCell(String id, double x, double y) {
        super(id, x, y, 10);
        Circle view = new Circle(size / 2D);
        view.setStroke(Color.DODGERBLUE);
        view.setFill(Color.DODGERBLUE);
        setView(view);
    }

}