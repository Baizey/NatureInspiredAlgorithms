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
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import natural.ACO.Colony;
import natural.ACO.Node;
import natural.AbstractPopulation;
import natural.Islands;
import natural.factory.ColonyFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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

    private final Wrap abortPressed = new Wrap<>(Boolean.FALSE);

    private void gotoMenuOptions(Algorithm algorithm, Problem problem) {
        // Kill old processes if they're running
        try {
            abortPressed.set(true);
            Thread.sleep(250);
        } catch (Exception ignored) {
        }
        content.setCenter(new Label("Waiting for input"));
        options.getChildren().clear();
        abortPressed.set(false);
        options.setPadding(new Insets(10, 0, 10, 10));
        options.setSpacing(10D);

        // Title
        Label title = new Label(algorithm.abbrivation + ": " + problem.abbrivation);
        title.setFont(new Font(30D));

        // Parallization options
        Label optimizationOptionLabel = new Label("Thread usage: ");
        ComboBox<String> optimizationOption = new ComboBox<>(FXCollections.observableArrayList("None", "Parallel", "Islands"));
        optimizationOption.getSelectionModel().selectFirst();
        Label optimizationThreadLabel = new Label("Thread/Island count: ");
        ComboBox<String> optimizationThreadCount = new ComboBox<>(FXCollections.observableArrayList(
                IntStream.range(1, 16).mapToObj(i -> i == 1 ? "Auto" : String.valueOf(i)).toArray(String[]::new)));
        optimizationThreadCount.getSelectionModel().selectFirst();
        options.getChildren().addAll(
                title,
                new HBox(optimizationOptionLabel, optimizationOption),
                new HBox(optimizationThreadLabel, optimizationThreadCount));

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
            abortPressed.set(true);
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
                Label percentLabel = new Label("Percent change: ");
                NumberTextField percentOption = new NumberTextField(0.05, 0, 1);
                Label generationLabel = new Label("Population per generation: ");
                NumberTextField generationOption = new NumberTextField(1000, 1, 100000);
                options.getChildren().addAll(
                        new HBox(percentLabel, percentOption),
                        new HBox(generationLabel, generationOption)
                );

                var useCircle = new Wrap<>(true);
                Label genesizeLabel = new Label("Genesize: ");
                NumberTextField geneSizeOption = new NumberTextField(50, 1, 100000);
                Label randomGraphLabel = new Label("Random graph size: ");
                NumberTextField randomGraphOption = new NumberTextField(20, 3, 100);
                Label graphLabel = new Label("Graph: ");
                ComboBox<String> graphOption = new ComboBox<>(FXCollections.observableArrayList("Random"));
                switch (problem.abbrivation) {
                    case "OM":
                        options.getChildren().addAll(new HBox(genesizeLabel, geneSizeOption));
                        break;
                    case "TSPP":
                        useCircle.set(false);
                    case "TSPC":
                        graphOption.getSelectionModel().selectFirst();
                        options.getChildren().addAll(
                                new HBox(randomGraphLabel, randomGraphOption),
                                new HBox(graphLabel, graphOption)
                        );
                        break;
                }

                var best = new Wrap<>(-1L);
                var island = new Wrap<Islands>(null);
                var colony = new Wrap<Colony>(null);
                var useParallel = new Wrap<>(false);

                runButton.setOnAction(action -> {
                    runButton.setDisable(true);
                    abortButton.setDisable(false);
                    island.set(null);
                    colony.set(null);
                    best.set(-1L);



                    useParallel.set(optimizationOption.getValue().equalsIgnoreCase("Parallel"));
                    int threads = 1;
                    if(useParallel.get()) {
                        if(optimizationThreadCount.getSelectionModel().getSelectedItem().equalsIgnoreCase("Auto"))
                            threads = Runtime.getRuntime().availableProcessors();
                        else
                            threads = Integer.parseInt(optimizationThreadCount.getSelectionModel().getSelectedItem());
                    }
                    // Make graph
                    double[][] points = new double[randomGraphOption.getNumberAsInt()][];
                    Random random = new Random();
                    for(int i = 0; i <points.length; i++)
                        points[i] = new double[]{
                            random.nextInt((int) content.getWidth()),
                            random.nextInt((int) content.getHeight())};
                    if(optimizationOption.getValue().equalsIgnoreCase("Islands")) {
                        var counter = new Wrap<>(0);
                        island.set(new Islands(
                                islands -> {
                                    counter.set(counter.get() + 1);
                                    if(counter.get() > 100) {
                                        counter.set(0);
                                        Colony[] colonies = (Colony[]) islands;
                                        int curr = 0;
                                        for(int i = 1; i < islands.length; i++)
                                            if(islands[i].getBestFitness() > islands[curr].getBestFitness())
                                                curr = i;
                                        for(int i = 0; i < islands.length; i++)
                                            if(i != curr)
                                                colonies[i].copyGraphProgression(colonies[curr]);
                                    }
                                },
                                AbstractPopulation::evolve,
                                IntStream.range(0, threads)
                                        .mapToObj(i -> ColonyFactory.travelingSalesman(useCircle.get(), points, generationOption.getNumberAsInt(), percentOption.getNumberAsDouble(), 1))
                                        .toArray(Colony[]::new)
                        ));
                        colony.set((Colony) island.get().getIsland(0));
                    } else
                        colony.set(ColonyFactory.travelingSalesman(useCircle.get(), points, generationOption.getNumberAsInt(), percentOption.getNumberAsDouble(), threads));
                    drawGraph(colony.get().getGraph());

                    Task task = new Task() {
                        protected Object call() {
                            try {
                                (island.get() != null ? island : colony).get().evolveUntilNoProgressParallel(100, (c) -> {
                                    if ((boolean) abortPressed.get()) {
                                        abortPressed.set(false);
                                        throw new InterruptedException("Aborted");
                                    }
                                    updateMessage(String.valueOf((best.set(Math.max(best.get(), c.getBestFitness())))));
                                });
                            } catch (InterruptedException ignored) {
                            }
                            runButton.setDisable(false);
                            abortButton.setDisable(true);
                            return null;
                        }
                    };


                    task.messageProperty().addListener(((observable, oldValue, newValue) -> updateGraph(colony.get(), useCircle.get())));
                    new Thread(task).start();
                });


                break;
        }

        options.getChildren().addAll(runButton, abortButton);
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

class Wrap<T> {
    private T value;

    Wrap(T value) {
        set(value);
    }

    T set(T value) {
        this.value = value;
        return value;
    }

    T get() {
        return value;
    }
}