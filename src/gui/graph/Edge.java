package gui.graph;

import gui.graph.cell.Cell;
import javafx.scene.Group;
import javafx.scene.shape.Line;

public class Edge extends Group {

    protected Cell source;
    protected Cell target;

    Line line;

    public Edge(Cell source, Cell target) {
        this.source = source;
        this.target = target;
        source.addCellChild(target);
        target.addCellParent(source);
        line = new Line();
        double factor = 6D;
        line.startXProperty().bind(source.layoutXProperty().add(source.getWidth() / factor));
        line.startYProperty().bind(source.layoutYProperty().add(source.getHeight() / factor));
        line.endXProperty().bind(target.layoutXProperty().add(target.getWidth() / factor));
        line.endYProperty().bind(target.layoutYProperty().add(target.getHeight() / factor));
        getChildren().add(line);
    }

}