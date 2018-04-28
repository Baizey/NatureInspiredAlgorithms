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
import natural.GA.Population;
import natural.GA.crossover.Crossover;
import natural.GA.fitness.Fitness;
import natural.GA.mutations.Mutation;
import natural.GA.preCalc.PreCalcs;
import natural.GA.select.Selection;
import natural.factory.PopulationFactory;

import java.util.ArrayList;
import java.util.Arrays;
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
    private final Wrap version = new Wrap<>(0);

    private void gotoMenuOptions(Algorithm algorithm, Problem problem) {
        // Kill old processes if they're running
        version.set((Integer) version.get() + 1); // Tell any old algorithm threads they're to terminate
        content.setCenter(new Label("Waiting for input"));
        options.getChildren().clear();
        abortPressed.set(false);
        options.setPadding(new Insets(10, 0, 10, 10));
        options.setSpacing(10D);

        // Title
        Label title = new Label(algorithm.abbrivation + ": " + problem.abbrivation);
        title.setFont(new Font(30D));

        // Thread options
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


        NumberTextField geneSizeOption = new NumberTextField(50, 1, 100000);
        ComboBox<String> biasOption = new ComboBox<>(FXCollections.observableArrayList("None", "Random"));

        // TSP option
        var useCircle = new Wrap<>(true);

        // Genetic algorithm buttons
        NumberTextField goal = new NumberTextField(50, 1, 1000);
        NumberTextField popSize = new NumberTextField(50, 1, 1000);
        ComboBox<String> selection = new ComboBox<>(FXCollections.observableArrayList("Random"));
        ComboBox<String> crossover = new ComboBox<>(FXCollections.observableArrayList("Random"));
        ComboBox<String> mutation = new ComboBox<>(FXCollections.observableArrayList("Random"));
        TextField numArray = new TextField();

        // Ant colony optimization buttons
        NumberTextField percentOption = new NumberTextField(0.05, 0, 1);
        NumberTextField popOption = new NumberTextField(1000, 1, 100000);
        NumberTextField randomGraphOption = new NumberTextField(20, 3, 100);
        ComboBox<String> graphOption = new ComboBox<>(FXCollections.observableArrayList("Random"));

        // Figure out which buttons to display
        switch (algorithm.abbrivation) {
            case "GA":
                switch (problem.abbrivation) {
                    case "OM":
                        options.getChildren().addAll(
                                UICreater.numericalField("Genesize", geneSizeOption),
                                UICreater.dropdownMenu("Bias", biasOption));
                        break;
                    case "SS":
                        options.getChildren().addAll(
                                UICreater.numericalField("Goal", goal),
                                UICreater.textField("Numbers", numArray),
                                UICreater.numericalField("Pop size", popSize),
                                UICreater.dropdownMenu("Selection", selection),
                                UICreater.dropdownMenu("Crossover", crossover),
                                UICreater.dropdownMenu("Mutation", mutation));
                        break;
                }
                break;
            case "ACO":
                options.getChildren().addAll(
                        UICreater.numericalField("Percent change", percentOption),
                        UICreater.numericalField("Pop per generation", popOption));
                switch (problem.abbrivation) {
                    case "OM":
                        options.getChildren().addAll(
                                UICreater.numericalField("Genesize", geneSizeOption),
                                UICreater.dropdownMenu("Bias", biasOption));
                        break;
                    case "TSPP":
                        useCircle.set(false);
                    case "TSPC":
                        options.getChildren().addAll(
                                UICreater.numericalField("Random graph size", randomGraphOption),
                                UICreater.dropdownMenu("Graph", graphOption));
                        break;
                }
                break;
        }

        final int myVersion = (Integer) this.version.get();
        runButton.setOnAction(action -> {
            if(version.isSame(myVersion))
                return;

            final AbstractPopulation work;

            // Thread options
            final boolean useParallel = getSelected(optimizationOption).equalsIgnoreCase("Parallel");
            final boolean useIslands = getSelected(optimizationOption).equalsIgnoreCase("Islands");
            int choosenThreads = getSelected(optimizationThreadCount).equalsIgnoreCase("Auto")
                    ? Runtime.getRuntime().availableProcessors()
                    : Integer.parseInt(getSelected(optimizationThreadCount));
            if(!(useIslands || useIslands)) choosenThreads = 1;

            // Pop/Gene size options
            final int pops = popOption.getNumberAsInt();
            final int genes = geneSizeOption.getNumberAsInt();

            // ACO options
            final String graphType = getSelected(graphOption);
            final int randomGraphSize = randomGraphOption.getNumberAsInt();
            final double percentChanging = percentOption.getNumberAsDouble();

            // GA SS options
            final int goalChoice = goal.getNumberAsInt();
            int[] numArrayChoice;
            try {
                numArrayChoice = Pattern.compile(" ").splitAsStream(numArray.getCharacters().toString()).mapToInt(Integer::parseInt).toArray();
            } catch(Exception error){
                numArrayChoice = null;
            }
            final String selectionChoice = getSelected(selection);
            final String crossoverChoice = getSelected(crossover);
            final String mutationChoice = getSelected(mutation);

            AbstractPopulation initialPopulation = null;
            switch (algorithm.abbrivation){
                case "GA":
                    switch(problem.abbrivation){
                        case "OM":
                            initialPopulation = PopulationFactory.oneMax(genes);
                            break;
                        case "SS":
                            initialPopulation = new Population(
                                pops, 100, true, true, choosenThreads,
                                Mutation.get(mutationChoice),
                                Fitness.subsetSum(goalChoice, numArrayChoice),
                                Crossover.get(crossoverChoice),
                                Selection.get(selectionChoice),
                                PreCalcs.get(crossoverChoice, selectionChoice)
                            );
                            break;
                    }
                    break;
                case "ACO":
                    break;
            }

            if (useIslands) {
                // TODO: handle islands
            } else {
                work = initialPopulation;
            }

            Task task = new Task() {
                protected Object call() throws Exception {

                    return null;
                }
            };

            task.messageProperty().addListener(((observable, oldValue, newValue) ->{
                // TODO: handle updating UI
            }));
            new Thread(task).start();
        });

        options.getChildren().addAll(runButton, abortButton);
    }

    private String getSelected(ComboBox<String> box) {
        return box.getSelectionModel().getSelectedItem();
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

    boolean isSame(T value){
        if(this.value == null)
            return value == null;
        return this.value.equals(value);
    }
}