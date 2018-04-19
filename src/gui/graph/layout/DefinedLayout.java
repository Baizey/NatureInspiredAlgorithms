package gui.graph.layout;

import gui.graph.Graph;
import gui.graph.cell.Cell;

public class DefinedLayout extends Layout {
    private final Graph graph;

    public DefinedLayout(Graph graph) {
        this.graph = graph;
    }

    @Override
    public void execute() {
        for(Cell cell : graph.getModel().getAllCells())
            cell.relocate(cell.x, cell.y);
    }
}
