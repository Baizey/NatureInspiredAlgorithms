package gui.graph;

import gui.graph.cell.Cell;
import javafx.scene.Group;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;

public class Graph {

    MouseGestures mouseGestures;
    /**
     * the pane wrapper is necessary or else the scrollpane would always align
     * the top-most and left-most child to the top and left eg when you drag the
     * top child down, the entire scrollpane would move down
     */
    Pane cellLayer;
    private Model model;
    private ZoomableScrollPane scrollPane;

    public Graph() {

        this.model = new Model();

        Group canvas = new Group();
        cellLayer = new Pane();

        canvas.getChildren().add(cellLayer);

        mouseGestures = new MouseGestures(this);

        scrollPane = new ZoomableScrollPane(canvas);

        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

    }

    public ScrollPane getScrollPane() {
        return this.scrollPane;
    }

    public Pane getCellLayer() {
        return this.cellLayer;
    }

    public Model getModel() {
        return model;
    }

    public void beginUpdate() {
    }

    public void endUpdate() {
        getCellLayer().getChildren().addAll(model.getAddedEdges());
        getCellLayer().getChildren().addAll(model.getAddedCells());
        getCellLayer().getChildren().removeAll(model.getRemovedCells());
        getCellLayer().getChildren().removeAll(model.getRemovedEdges());

        for (Cell cell : model.getAddedCells())
            mouseGestures.makeDraggable(cell);

        getModel().attachOrphansToGraphParent(model.getAddedCells());
        getModel().disconnectFromGraphParent(model.getRemovedCells());
        getModel().merge();
    }

    public double getScale() {
        return this.scrollPane.getScaleValue();
    }
}