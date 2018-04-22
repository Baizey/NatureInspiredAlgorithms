package gui;

import gui.data.Algorithm;
import gui.data.Problem;
import gui.graph.Graph;
import gui.graph.Model;
import gui.graph.cell.CircleCell;
import gui.graph.layout.DefinedLayout;
import gui.graph.layout.Layout;
import javafx.application.Application;
import javafx.beans.property.DoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import natural.ACO.Colony;
import natural.ACO.Node;
import natural.AbstractPopulation;
import natural.factory.ColonyFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class App extends Application {

    ObservableList<ForceUpdater> items = FXCollections.observableArrayList(ForceUpdater.extractor());
    ForceUpdater updater = new ForceUpdater();

    private AbstractPopulation population;
    private double[][] TSP;
    private BorderPane content;
    private VBox options;

    private static final Algorithm[] algorithms = {
            new Algorithm(
                    "Genetic Algorithm",
                    new Problem("OneMax"),
                    new Problem("Subset Sum")),
            new Algorithm(
                    "Ant Colony Optimization",
                    new Problem("OneMax"),
                    new Problem("Traveling Salesman Problem Circle"),
                    new Problem("Traveling Salesman Problem Path")),
    };

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        BorderPane root = makeSettingMenu();
        root.setTop(makeMenuBar());
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

    private BoolWrap abortPressed = new BoolWrap(false);

    private void gotoMenuOptions(Algorithm algorithm, Problem problem) {
        // Kill old processes if they're running
        try {
            abortPressed.value = true;
            Thread.sleep(250);
        } catch (Exception ignored) {
        }
        content.setCenter(new Label("Waiting for input"));
        options.getChildren().clear();
        abortPressed = new BoolWrap(false);
        options.setPadding(new Insets(10, 0, 10, 10));
        options.setSpacing(10D);

        // Title
        Label title = new Label(algorithm.abbrivation + ": " + problem.abbrivation);
        title.setFont(new Font(30D));
        options.getChildren().addAll(title);

        // Run button
        Button runButton = new Button("Run");
        GridPane.setHalignment(runButton, HPos.CENTER);
        runButton.setMinWidth(options.getWidth() - 40D);

        // Abort button
        Button abortButton = new Button("Abort");
        abortButton.setCancelButton(true);
        abortButton.setMinWidth(options.getWidth() - 40D);
        GridPane.setHalignment(abortButton, HPos.CENTER);
        abortButton.setDisable(true);
        abortButton.setOnAction(action -> {
            abortButton.setDisable(true);
            abortPressed.value = true;
        });


        switch (algorithm.abbrivation) {
            case "GA":
                switch (problem.abbrivation) {
                    case "OM":
                        break;
                    case "SSS":
                        break;
                }
                break;
            case "ACO":
                boolean useCircle = true;
                switch (problem.abbrivation) {
                    case "OM":
                        break;
                    case "TSPP":
                        useCircle = false;
                    case "TSPC":
                        TSP = new double[25][];
                        Random random = new Random();
                        for (int i = 0; i < TSP.length; i++)
                            TSP[i] = new double[]{random.nextInt(1000), random.nextInt(1000)};
                        Colony colony;
                        if (useCircle) colony = ColonyFactory.travelingSalesmanCircle(TSP, 10000, 0.05);
                        else colony = ColonyFactory.travelingSalesmanPath(TSP, 10000, 0.05);

                        drawGraph(colony.getGraph());
                        LongWrap best = new LongWrap(-1);
                        final boolean useCircleFinal = useCircle;
                        runButton.setOnAction(action -> {
                            runButton.setDisable(true);
                            abortButton.setDisable(false);
                            Task task = new Task() {
                                protected Object call() {
                                    try {
                                        colony.evolveUntilNoProgressParallel(100, (c) -> {
                                            if (abortPressed.value) {
                                                abortPressed.value = false;
                                                throw new InterruptedException("Aborted");
                                            }
                                            updateMessage(String.valueOf((best.value = Math.max(best.value, c.getBestFitness()))));
                                        });
                                    } catch (InterruptedException ignored) {
                                    }
                                    runButton.setDisable(false);
                                    abortButton.setDisable(true);
                                    return null;
                                }
                            };
                            task.messageProperty().addListener(((observable, oldValue, newValue) -> updateGraph(colony, useCircleFinal)));
                            new Thread(task).start();
                        });
                        break;
                }
                break;
        }

        options.getChildren().addAll(runButton, abortButton);
    }

    class LongWrap {
        long value;

        LongWrap(long value) {
            this.value = value;
        }
    }

    class BoolWrap {
        boolean value;

        BoolWrap(boolean value) {
            this.value = value;
        }
    }

    private BorderPane makeSettingMenu() {
        SplitPane splitPane = new SplitPane();
        options = new VBox();
        options.setMinWidth(0);
        content = new BorderPane();
        splitPane.getItems().addAll(options, content);
        DoubleProperty splitPaneDividerPosition = splitPane.getDividers().get(0).positionProperty();
        splitPaneDividerPosition.set(0.2);
        return new BorderPane(splitPane, null, null, null, null);
    }

    private Graph graph = null;

    private void drawGraph(Node[] nodes) {
        graph = new Graph();
        Model model = graph.getModel();
        graph.beginUpdate();

        for (int i = 0; i < nodes.length; i++) {
            double[] pos = Pattern.compile(" *, *").splitAsStream(nodes[i].name).mapToDouble(Double::parseDouble).toArray();
            model.addCell(new CircleCell(nodes[i].name, pos[0], pos[1]));
        }

        graph.endUpdate();
        Layout layout = new DefinedLayout(graph);
        layout.execute();
        content.setCenter(graph.getScrollPane());
    }

    private void updateGraph(Colony colony, boolean circle) {
        Model model = graph.getModel();
        String[] path = colony.nodePath();
        graph.beginUpdate();
        model.getRemovedEdges().addAll(model.getAllEdges());
        for (int i = 0; i < path.length - 1; i++)
            model.addEdge(path[i], path[i + 1]);
        if (circle)
            model.addEdge(path[path.length - 1], path[0]);
        graph.endUpdate();
    }
}
