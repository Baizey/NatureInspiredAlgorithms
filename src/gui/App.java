package gui;

import gui.graph.Graph;
import gui.graph.Model;
import gui.graph.cell.CircleCell;
import gui.graph.layout.DefinedLayout;
import gui.graph.layout.Layout;
import javafx.application.Application;
import javafx.beans.property.DoubleProperty;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lsm.helpers.IO.write.text.console.Note;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.stream.Collectors;

public class App extends Application {

    private static final Algorithm[] algorithms = {
            new Algorithm(
                    "Genetic Algorithm",
                    new Problem("OneMax"),
                    new Problem("Subset Sum")),
            new Algorithm(
                    "Ant Colony Optimization",
                    new Problem("OneMax"),
                    new Problem("Traveling Salesman Problem")),
    };

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        BorderPane root = makeSettingMenu();
        Graph graph = drawGraph();
        root.setTop(makeMenuBar());
        content.setCenter(graph.getScrollPane());
        var scene = new Scene(root, 1280, 720);
        primaryStage.setTitle("Nature Based Algorithm Visualization");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private MenuBar makeMenuBar() {
        var result = new MenuBar();
        var menuOptions = Arrays.stream(algorithms)
                .map(algorithm -> {
                    var menu = new Menu(algorithm.name);
                    for (var problem : algorithm.problems) {
                        var item = new MenuItem(problem.name);
                        item.setOnAction(event -> gotoMenuOptions(algorithm, problem));
                        menu.getItems().add(item);
                    }
                    return menu;
                }).collect(Collectors.toCollection(ArrayList::new));
        result.getMenus().addAll(menuOptions);
        return result;
    }

    private void gotoMenuOptions(Algorithm algorithm, Problem problem) {
        Note.writenl(algorithm.name + " -> " + problem.name);
    }

    private BorderPane content;
    private BorderPane makeSettingMenu() {
        ToggleButton settings = new ToggleButton("Settings");
        SplitPane splitPane = new SplitPane();

        TitledPane titledPane = new TitledPane("Options", new Label("An option"));
        VBox settingsPane = new VBox(titledPane);
        settingsPane.setMinWidth(0);
        content = new BorderPane();
        splitPane.getItems().addAll(settingsPane, content);
        DoubleProperty splitPaneDividerPosition = splitPane.getDividers().get(0).positionProperty();
        splitPaneDividerPosition.addListener((obs, oldPos, newPos) -> settings.setSelected(newPos.doubleValue() < 0.95));
        splitPaneDividerPosition.set(0.2);
        settings.setOnAction(event -> splitPane.setDividerPositions(settings.isSelected() ? 0.2 : 0.0));
        return new BorderPane(splitPane, new HBox(settings), null, null, null);
    }

    private Graph drawGraph() {
        Graph graph = new Graph();
        Model model = graph.getModel();
        Random random = new Random();
        graph.beginUpdate();

        for (int i = 0; i <= 10; i++)
            model.addCell(new CircleCell("Cell " + (char) ('A' + i), random.nextInt(500), random.nextInt(500)));
        for (int i = 0; i <= 10; i++)
            model.addEdge("Cell " + (char) ('A' + i), "Cell " + (char) ('A' + (i == 10 ? -1 : i) + 1));

        graph.endUpdate();
        Layout layout = new DefinedLayout(graph);
        layout.execute();
        return graph;
    }
}
