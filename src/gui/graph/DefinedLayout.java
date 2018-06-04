package gui.graph;

public class DefinedLayout {
    private final Graph graph;

    public DefinedLayout(Graph graph) {
        this.graph = graph;
    }

    private int inserted = 0;
    public void execute() {
        for(; inserted < graph.getModel().getAllCells().size(); inserted++) {
            var cell = graph.getModel().getAllCells().get(inserted);
            cell.relocate(cell.x - cell.size / 2, cell.y - cell.size / 2);
        }
    }
}
