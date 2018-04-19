package gui.graph.layout;

import gui.graph.Graph;
import gui.graph.cell.Cell;

import java.util.Random;

public class RandomLayout extends Layout {
    private final Graph graph;
    private final Random random = new Random();
    public RandomLayout(Graph graph) {
        this.graph = graph;
    }
    public void execute() {
        for(Cell cell : graph.getModel().getAllCells())
            cell.relocate(random.nextInt(500), random.nextInt(500));
    }
}