package gui.graph;

import gui.graph.cell.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Model {

    Cell graphParent;

    List<Cell> allCells;
    List<Cell> addedCells;
    List<Cell> removedCells;

    List<Edge> allEdges;
    List<Edge> addedEdges;
    List<Edge> removedEdges;

    Map<String, Cell> cellMap; // <id,cell>

    public Model() {
        graphParent = new Cell("_ROOT_", 0, 0, 0);
        // clear model, create lists
        clear();
    }

    public void clear() {
        allCells = new ArrayList<>();
        addedCells = new ArrayList<>();
        removedCells = new ArrayList<>();

        allEdges = new ArrayList<>();
        addedEdges = new ArrayList<>();
        removedEdges = new ArrayList<>();

        cellMap = new HashMap<>(); // <id, cell>
    }

    public void clearAddedLists() {
        addedCells.clear();
        addedEdges.clear();
    }

    public List<Cell> getAddedCells() {
        return addedCells;
    }

    public List<Cell> getRemovedCells() {
        return removedCells;
    }

    public List<Cell> getAllCells() {
        return allCells;
    }

    public List<Edge> getAddedEdges() {
        return addedEdges;
    }

    public List<Edge> getRemovedEdges() {
        return removedEdges;
    }

    public List<Edge> getAllEdges() {
        return allEdges;
    }

    public void addCell(String id, CellType type, double x, double y) {
        switch (type) {
            case RECTANGLE:
                RectangleCell rectangleCell = new RectangleCell(id, x, y);
                addCell(rectangleCell);
                break;
            case TRIANGLE:
                TriangleCell triangleCell = new TriangleCell(id, x, y);
                addCell(triangleCell);
                break;
            case CIRCLE:
                CircleCell circleCel1 = new CircleCell(id, x, y);
                addCell(circleCel1);
                break;
            default:
                throw new UnsupportedOperationException("Unsupported type: " + type);
        }
    }

    public void addCell(Cell cell) {
        addedCells.add(cell);
        cellMap.put(cell.getCellId(), cell);

    }

    public void addEdge(Cell source, Cell target) {
        addedEdges.add(new Edge(source, target));
    }

    public void addEdge(String sourceId, String targetId) {
        addedEdges.add(new Edge(cellMap.get(sourceId), cellMap.get(targetId)));
    }

    /**
     * Attach all cells which don't have a parent to graphParent
     *
     * @param cellList
     */
    public void attachOrphansToGraphParent(List<Cell> cellList) {
        for (Cell cell : cellList)
            if (cell.getCellParents().size() == 0)
                graphParent.addCellChild(cell);
    }

    /**
     * Remove the graphParent reference if it is set
     *
     * @param cellList
     */
    public void disconnectFromGraphParent(List<Cell> cellList) {
        for (Cell cell : cellList)
            graphParent.removeCellChild(cell);
    }

    public void merge() {

        // cells
        allCells.addAll(addedCells);
        allCells.removeAll(removedCells);

        addedCells.clear();
        removedCells.clear();

        // edges
        allEdges.addAll(addedEdges);
        allEdges.removeAll(removedEdges);

        addedEdges.clear();
        removedEdges.clear();

    }
}