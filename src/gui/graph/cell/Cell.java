package gui.graph.cell;

import javafx.scene.Node;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.List;

public class Cell extends Pane {
    private String cellId;
    final double size;
    public final double x, y;
    private List<Cell> children = new ArrayList<>();
    private List<Cell> parents = new ArrayList<>();
    private Node view;
    public Cell(String id, double x, double y, double size) {
        this.size = size;
        this.cellId = id;
        this.x = (x - size / 2D);
        this.y = (y - size / 2D);
        Tooltip.install(this, new Tooltip(id));
    }
    public void addCellChild(Cell cell) {
        children.add(cell);
    }
    public List<Cell> getCellChildren() {
        return children;
    }
    public void addCellParent(Cell cell) {
        parents.add(cell);
    }
    public List<Cell> getCellParents() {
        return parents;
    }
    public void removeCellChild(Cell cell) {
        children.remove(cell);
    }
    public Node getView() {
        return this.view;
    }
    public void setView(Node view) {
        this.view = view;
        getChildren().add(view);

    }
    public String getCellId() {
        return cellId;
    }
}