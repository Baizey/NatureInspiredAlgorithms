package gui;

import gui.data.Algorithm;
import gui.data.Problem;
import gui.graph.Graph;
import gui.graph.Model;
import gui.graph.cell.CircleCell;
import gui.graph.cell.TriangleCell;
import gui.graph.layout.DefinedLayout;
import gui.graph.layout.Layout;
import javafx.application.Application;
import javafx.beans.property.DoubleProperty;
import javafx.collections.FXCollections;
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
import lsm.helpers.IO.write.text.console.Note;
import natural.ACO.Colony;
import natural.ACO.Node;
import natural.AbstractPopulation;
import natural.Action;
import natural.GA.Population;
import natural.GA.crossover.Crossover;
import natural.GA.fitness.Fitness;
import natural.GA.mutations.Mutation;
import natural.GA.preCalc.PreCalcs;
import natural.GA.select.Selection;
import natural.factory.ColonyFactory;
import natural.factory.PopulationFactory;
import natural.islands.Convergence;
import natural.islands.ConvergenceInterface;
import natural.islands.Islands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class App extends Application {
    public static final Problem
            OneMax = new Problem("OneMax"),
            TSPCircle = new Problem("Traveling Salesman Problem Circle"),
            TSPPath = new Problem("Traveling Salesman Problem Path"),
            LeadingOnes = new Problem("Leading Ones"),
            SubsetSum = new Problem("Subset Sum");
    public static final Algorithm
            GeneticAlgorithm = new Algorithm("Genetic Algorithm", OneMax, SubsetSum),
            AntColonyOptimization = new Algorithm("Ant Colony Optimization", OneMax, TSPCircle, TSPPath);
    private static final Algorithm[] algorithms = {GeneticAlgorithm, AntColonyOptimization};

    private BorderPane content;
    private VBox options;

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

    private final Wrap<Integer> version = new Wrap<>(0);

    private void gotoMenuOptions(Algorithm algorithm, Problem problem) {
        // Kill old processes if they're running
        version.value++;
        content.setCenter(new Label("Waiting for input"));
        content.setBottom(new Label("Best fitness will show here"));
        options.getChildren().clear();
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
            runButton.setDisable(false);
            abortButton.setDisable(true);
            version.value++;
        });


        NumberTextField geneSizeOption = new NumberTextField(50, 1, 100000);
        ComboBox<String> biasOption = new ComboBox<>(FXCollections.observableArrayList("None", "Random"));

        // TSP option
        var useCircle = new Wrap<>(true);

        // Genetic algorithm buttons
        NumberTextField goal = new NumberTextField(50, 1, 1000);
        NumberTextField popSize = new NumberTextField(50, 1, 1000);
        ComboBox<String> selection = new ComboBox<>(FXCollections.observableArrayList("Best", "Stochastic", "Random"));
        ComboBox<String> crossover = new ComboBox<>(FXCollections.observableArrayList("copy best", "half and half", "half and half random", "fitness determined half and half"));
        ComboBox<String> mutation = new ComboBox<>(FXCollections.observableArrayList("none", "flip one", "flip two", "flip three", "(1 + 1)"));
        ComboBox<String> oneMaxType = new ComboBox<>(FXCollections.observableArrayList(OneMax.name, LeadingOnes.name));
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
                                UICreater.dropdownMenu("Fitness type", oneMaxType),
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

        final int myVersion = this.version.get();
        runButton.setOnAction(action -> {
            runButton.setDisable(true);
            abortButton.setDisable(false);

            final AbstractPopulation work;

            // Thread options
            final boolean useParallel = getSelected(optimizationOption).equalsIgnoreCase("Parallel");
            final boolean useIslands = getSelected(optimizationOption).equalsIgnoreCase("Islands");
            int choosenThreads = getSelected(optimizationThreadCount).equalsIgnoreCase("Auto")
                    ? Runtime.getRuntime().availableProcessors()
                    : Integer.parseInt(getSelected(optimizationThreadCount));
            if (!(useIslands || useIslands)) choosenThreads = 1;

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
            } catch (Exception error) {
                numArrayChoice = null;
            }
            final String selectionChoice = getSelected(selection);
            final String crossoverChoice = getSelected(crossover);
            final String mutationChoice = getSelected(mutation);

            var initialPopulation = new Wrap<AbstractPopulation>(null);
            Node.resetId();
            switch (algorithm.abbrivation) {
                case "GA":
                    switch (problem.abbrivation) {
                        case "OM":
                            if (oneMaxType.getSelectionModel().getSelectedItem().equalsIgnoreCase(OneMax.name))
                                initialPopulation.value = PopulationFactory.oneMax(genes, getSelected(biasOption).equalsIgnoreCase("random"));
                            else
                                initialPopulation.value = PopulationFactory.leadingOnes(genes, getSelected(biasOption).equalsIgnoreCase("random"));
                            break;
                        case "SS":
                            initialPopulation.value = new Population(
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
                    switch (problem.abbrivation) {
                        case "OM":
                            initialPopulation.value = ColonyFactory.oneMaxBinary(choosenThreads, genes, pops, percentChanging);
                            break;
                        case "TSPP":
                            useCircle.value = false;
                        case "TSPC":
                            double[][] points = new double[randomGraphSize][];
                            Random random = new Random();
                            for (int i = 0; i < points.length; i++)
                                points[i] = new double[]{random.nextInt((int) content.getWidth()), random.nextInt((int) content.getHeight())};
                            initialPopulation.value = ColonyFactory.travelingSalesman(useCircle.value, points, pops, percentChanging, choosenThreads);
                            break;
                    }
                    break;
            }

            if (useIslands) {
                AbstractPopulation[] newIslands = new AbstractPopulation[choosenThreads];
                newIslands[0] = initialPopulation.value;
                for (int i = 1; i < newIslands.length; i++) {
                    newIslands[i] = algorithm.abbrivation.equalsIgnoreCase(GeneticAlgorithm.abbrivation)
                            ? new Population((Population) initialPopulation.value)
                            : new Colony((Colony) initialPopulation.value);
                    if (algorithm.abbrivation.equalsIgnoreCase(AntColonyOptimization.abbrivation)) {
                        Node[] oldG = ((Colony) newIslands[0]).getGraph();
                        Node[] newG = ((Colony) newIslands[i]).getGraph();
                        for (int j = 0; j < newG.length; j++) {
                            Node[] oldE = oldG[j].getEdges();
                            Node[] newE = newG[j].getEdges();
                            for (int k = 0; k < newE.length; k++)
                                newE[k] = newG[oldE[k].getId()];
                        }
                    }
                }

                ConvergenceInterface convergence = algorithm.abbrivation.equalsIgnoreCase(GeneticAlgorithm.abbrivation)
                        ? Convergence.keepBestAfterPopulationX(100)
                        : Convergence.keepBestAfterColonyX(100);
                work = new Islands(convergence, AbstractPopulation::evolve, newIslands);
            } else {
                work = initialPopulation.value;
            }
            Task task = new Task() {
                protected Object call() throws Exception {
                    var best = new Wrap<>(-1L);
                    Action action = pop -> {
                        if (!version.isSame(myVersion))
                            throw new InterruptedException("Halting");
                        if (pop.getBestFitness() > best.value) {

                            Note.writenl(pop.getBest().getDna());

                            best.set(pop.getBestFitness());
                            updateMessage(best.toString());
                            Thread.sleep(0);
                        }
                    };
                    switch (problem.abbrivation) {
                        case "OM":
                            if (useIslands)
                                work.evolveUntilGoal(genes, action);
                            else if (useParallel)
                                work.evolveUntilGoalParallel(genes, action);
                            else
                                work.evolveUntilGoal(genes, action);
                            break;
                        default:
                            if (useIslands)
                                work.evolveUntilNoProgressParallel(100, action);
                            else if (useParallel)
                                work.evolveUntilNoProgressParallel(100, action);
                            else
                                work.evolveUntilNoProgress(100, action);
                    }
                    abortButton.setDisable(true);
                    runButton.setDisable(false);
                    return null;
                }
            };

            task.messageProperty().addListener(((observable, oldValue, newValue) -> {
                switch (algorithm.abbrivation) {
                    case "GA":
                        switch (problem.abbrivation) {
                            case "OM":
                                updateOMGraph((Population) initialPopulation.value);
                                break;
                        }
                        break;
                    case "ACO":
                        switch (problem.abbrivation) {
                            case "OM":
                                updateOMGraph((Colony) initialPopulation.value);
                                break;
                            case "TSPP":
                            case "TSPC":
                                updateTSPGraph((Colony) initialPopulation.value, useCircle.value);
                                break;
                        }
                        break;
                }
            }));
            drawTSPGraph(new Node[0]);
            switch (problem.abbrivation) {
                case "OM":
                    drawOMEdges();
                    break;
                case "TSPP":
                case "TSPC":
                    drawTSPGraph(((Colony) initialPopulation.value).getGraph());
                    break;
            }
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

    private void drawTSPGraph(Node[] nodes) {
        graph = new Graph();
        Model model = graph.getModel();
        graph.beginUpdate();

        for (Node node : nodes) {
            double[] pos = Pattern.compile(" *, *").splitAsStream(node.name).mapToDouble(Double::parseDouble).toArray();
            model.addCell(new CircleCell(node.name, pos[0], pos[1]));
        }

        graph.endUpdate();
        Layout layout = new DefinedLayout(graph);
        layout.execute();
        content.setCenter(graph.getScrollPane());
    }

    private void drawOMEdges() {
        var points = new double[][]{
                {20D, 20D + content.getHeight() / 2D},
                {20D + content.getWidth() / 2D, 20D + content.getHeight()},
                {20D + content.getWidth(), 20D + content.getHeight() / 2D},
                {20D + content.getWidth() / 2D, 20D}
        };
        Model model = graph.getModel();
        graph.beginUpdate();
        TriangleCell[] cells = new TriangleCell[points.length];
        for (int i = 0; i < points.length; i++) {
            cells[i] = new TriangleCell("", points[i][0], points[i][1]);
            cells[i].setVisible(false);
            model.addCell(cells[i]);
        }
        for (int i = 0; i < cells.length; i++) {
            model.addEdge(cells[i], cells[(i + 1) % cells.length]);
        }
        graph.endUpdate();
    }

    private void updateTSPGraph(Colony colony, boolean circle) {
        Model model = graph.getModel();
        String[] path = colony.nodePath();
        graph.beginUpdate();
        model.getRemovedEdges().addAll(model.getAllEdges());
        for (int i = 0; i < path.length - 1; i++)
            model.addEdge(path[i], path[i + 1]);
        if (circle)
            model.addEdge(path[path.length - 1], path[0]);

        long cost = Integer.MAX_VALUE - colony.getBestFitness();
        content.setBottom(new Label(String.valueOf(cost)));

        graph.endUpdate();
    }

    private void updateOMGraph(Colony population) {
        var path = population.edgePath();
        var ones = Arrays.stream(path).filter(s -> s.charAt(0) == '1').count();
        var dna = new boolean[path.length];
        for (var i = 0; i < dna.length; i++) dna[i] = path[i].charAt(0) == '1';
        var pos = getPos(dna);
        updateOMGraph(pos, (int) ones, population.getBest().getLength());
        long cost = population.getBestFitness();
        content.setBottom(new Label(String.valueOf(cost)));
    }

    private void updateOMGraph(Population population) {
        updateOMGraph(
                getPos(population.getBestDna().toArray()),
                population.getBestDna().cardinality(),
                population.getBest().getLength());
        long cost = population.getBestFitness();
        content.setBottom(new Label(String.valueOf(cost)));
    }

    private void updateOMGraph(double heightProgress, int ones, int totalGenes) {
        Model model = graph.getModel();
        double progress = (double) ones / (double) totalGenes;
        double x = (content.getWidth()) * progress;
        double y = content.getHeight() / 2D;
        y -= (y * (1D - Math.abs(1D - progress * 2D)) * ((heightProgress * 2D - 1D)));
        graph.beginUpdate();
        model.addCell(new CircleCell("", x, y));
        graph.endUpdate();
        Layout layout = new DefinedLayout(graph);
        layout.execute();
    }

    private static double getPos(boolean[] dna) {
        double  max = 0, min = 0,
                count = 0;
        int maxValue = dna.length, minValue = 1;
        for (int i = 0; i < dna.length; i++) {
            if (dna[i]) {
                count += i + 1;
                min += minValue++;
                max += maxValue--;
            }
        }
        return (count - min) / (max - min);
    }

    public static void main(String[] args) {
        launch(args);
    }

}

