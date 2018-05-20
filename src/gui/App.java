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
import javafx.scene.chart.LineChart;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import lsm.helpers.utils.Wrap;
import natural.ACO.Colony;
import natural.ACO.Edge;
import natural.ACO.Node;
import natural.ACO.NodeBias;
import natural.AbstractPopulation;
import natural.GA.*;
import natural.benchmark.GraphingData;
import natural.factory.ColonyFactory;
import natural.factory.IslandFactory;
import natural.factory.PopulationFactory;
import natural.interfaces.Action;
import natural.islands.Islands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class App extends Application {
    public static final Problem
            OneMax = new Problem("OneMax"),
            TSPCircle = new Problem("Traveling Salesman Problem Circle"),
            TSPPath = new Problem("Traveling Salesman Problem Path"),
            LeadingOnes = new Problem("Leading Ones"),
            SubsetSum = new Problem("Subset Sum"),
            benchmark = new Problem("benchmark");
    public static final Algorithm
            GeneticAlgorithm = new Algorithm("Genetic Algorithm", OneMax, SubsetSum),
            AntColonyOptimization = new Algorithm("Ant Colony Optimization", TSPCircle, TSPPath),
            precalced = new Algorithm("Precalculated", benchmark);
    private static final Algorithm[] algorithms = {GeneticAlgorithm, AntColonyOptimization, precalced};

    private static final HashMap<String, String> tspMaps = new HashMap<>() {{
        put("Berlin52", "565.0 575.0\n25.0 185.0\n345.0 750.0\n945.0 685.0\n845.0 655.0\n880.0 660.0\n25.0 230.0\n525.0 1000.0\n580.0 1175.0\n650.0 1130.0\n1605.0 620.0\n1220.0 580.0\n1465.0 200.0\n1530.0 5.0\n845.0 680.0\n725.0 370.0\n145.0 665.0\n415.0 635.0\n510.0 875.0\n560.0 365.0\n300.0 465.0\n520.0 585.0\n480.0 415.0\n835.0 625.0\n975.0 580.0\n1215.0 245.0\n1320.0 315.0\n1250.0 400.0\n660.0 180.0\n410.0 250.0\n420.0 555.0\n575.0 665.0\n1150.0 1160.0\n700.0 580.0\n685.0 595.0\n685.0 610.0\n770.0 610.0\n795.0 645.0\n720.0 635.0\n760.0 650.0\n475.0 960.0\n95.0 260.0\n875.0 920.0\n700.0 500.0\n555.0 815.0\n830.0 485.0\n1170.0 65.0\n830.0 610.0\n605.0 625.0\n595.0 360.0\n1340.0 725.0\n1740.0 245.0");
    }};

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
        version.set(version.get() + 1);
        content.setCenter(new Label("Waiting for input"));
        content.setBottom(new Label("Best fitness will show here"));
        options.getChildren().clear();
        options.setPadding(new Insets(10, 0, 10, 10));
        options.setSpacing(10D);

        // Title
        Label title = new Label(algorithm.abbrivation + ": " + problem.abbrivation);
        title.setFont(new Font(30D));

        // Thread options
        var waitForUi = new ComboBox<>(FXCollections.observableArrayList("Yes", "No"));
        var optimizationOption = new ComboBox<>(FXCollections.observableArrayList("None", "Parallel", "Islands"));
        optimizationOption.getSelectionModel().selectFirst();
        var optimizationThreadCount = new ComboBox<>(FXCollections.observableArrayList(
                IntStream.range(1, 16).mapToObj(i -> i == 1 ? "Auto" : String.valueOf(i)).toArray(String[]::new)));
        optimizationThreadCount.getSelectionModel().selectFirst();

        // Benchmark button
        Button benchButton = new Button("Display");
        GridPane.setHalignment(benchButton, HPos.CENTER);
        benchButton.setMinWidth(options.getWidth() - 40D);
        ComboBox<String> benchmark = new ComboBox<>(FXCollections.observableArrayList(
                "OneMax",
                "LeadingOnes",
                "Subset sum",
                "TSPC",
                "TSPP"));
        benchButton.setOnAction(event -> {
            String filename = "";
            String context = "";
            switch(getSelected(benchmark).toLowerCase()){
                case "onemax":
                    filename = "OMAvg";
                    context = "Expected: e * n * log(n)";
                    break;
                case "leadingones":
                    filename = "LOAvg";
                    context = "Expected: 0.86 * n^2";
                    break;
                case "subset sum":
                    filename = "SSAvg";
                    context = "Expected: ?";
                    break;
                case "tspc":
                    filename = "TSPC";
                    context = "Expected: Upper bound: (sqrt(2n) + 1.75) * 1000; Lower bound: (0.7078 * sqrt(n) + 0.551) * 1000";
                    break;
                case "tspp":
                    filename = "TSPP";
                    context = "Expected: Upper bound: (sqrt(2n + 2) + 1.75) * 1000; Lower bound: (0.7078 * sqrt(n + 1) + 0.551) * 1000";
                    break;
            }
            LineChart chart = null;
            try { chart = GraphingData.getDisplay(filename); } catch (Exception e) { e.printStackTrace(); }
            content.setCenter(chart);
            content.setBottom(new Label(context));
        });

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
            version.set(version.get() + 1);
            runButton.setDisable(false);
        });

        NumberTextField geneSizeOption = new NumberTextField(50, 1, 100000);
        ComboBox<String> OMBiasOption = new ComboBox<>(FXCollections.observableArrayList("None", "Random"));
        ComboBox<String> SSBiasOption = new ComboBox<>(FXCollections.observableArrayList("None", "Random"));

        // TSP option
        var useCircle = new Wrap<>(true);

        // Genetic algorithm buttons
        NumberTextField goal = new NumberTextField(50, 1, Integer.MAX_VALUE);
        NumberTextField popSize = new NumberTextField(50, 1, 10000);
        ComboBox<String> selection = new ComboBox<>(FXCollections.observableArrayList("Best", "Stochastic", "Random"));
        ComboBox<String> crossover = new ComboBox<>(FXCollections.observableArrayList("copy best", "half and half", "half and half random", "fitness determined half and half"));
        ComboBox<String> mutation = new ComboBox<>(FXCollections.observableArrayList("none", "flip one", "flip two", "flip three", "(1 + 1)"));
        ComboBox<String> oneMaxType = new ComboBox<>(FXCollections.observableArrayList(OneMax.name, LeadingOnes.name));
        NumberTextField SSNums = new NumberTextField(100, 10, 100000);

        // Ant colony optimization buttons
        NumberTextField percentOption = new NumberTextField(0.05, 0, 1);
        NumberTextField popOption = new NumberTextField(1000, 1, 100000);
        NumberTextField maxStagnation = new NumberTextField(100, 1, 10000);
        NumberTextField islandConverging = new NumberTextField(100, 1, 10000);
        NumberTextField randomGraphOption = new NumberTextField(20, 3, 100);
        ComboBox<String> ACOMutationOption = new ComboBox<>(FXCollections.observableArrayList("None", "2-opt"));
        ComboBox<String> graphOption = new ComboBox<>(FXCollections.observableArrayList("Random", "Berlin52"));
        ComboBox<String> TSPBiasOption = new ComboBox<>(FXCollections.observableArrayList("None", "Linear", "Polynomial"));

        // Figure out which buttons to display
        if(!algorithm.abbrivation.equalsIgnoreCase("p"))
            options.getChildren().addAll(
                    title,
                    UICreate.field("Wait for UI", waitForUi),
                    UICreate.field("Thread usage", optimizationOption),
                    UICreate.field("Thread/Island count", optimizationThreadCount),
                    UICreate.field("Island converging", islandConverging)
            );

        switch (algorithm.abbrivation) {
            case "P":
                options.getChildren().addAll(
                        UICreate.field("Choose", benchmark)
                );
                break;
            case "GA":
                switch (problem.abbrivation) {
                    case "OM":
                        options.getChildren().addAll(
                                UICreate.field("Fitness type", oneMaxType),
                                UICreate.field("Genesize", geneSizeOption),
                                UICreate.field("Bias", OMBiasOption));
                        break;
                    case "SS":
                        options.getChildren().addAll(
                                UICreate.field("Goal", goal),
                                UICreate.field("Numbers", SSNums),
                                UICreate.field("Pop size", popSize),
                                UICreate.field("Selection", selection),
                                UICreate.field("Crossover", crossover),
                                UICreate.field("Bias", SSBiasOption),
                                UICreate.field("Mutation", mutation));
                        break;
                }
                break;
            case "ACO":
                options.getChildren().addAll(
                        UICreate.field("Percent change", percentOption),
                        UICreate.field("Pop per generation", popOption),
                        UICreate.field("Max stagnation", maxStagnation),
                        UICreate.field("Mutation", ACOMutationOption));
                switch (problem.abbrivation) {
                    case "TSPP":
                        useCircle.set(false);
                    case "TSPC":
                        options.getChildren().addAll(
                                UICreate.field("Random graph size", randomGraphOption),
                                UICreate.field("Graph", graphOption),
                                UICreate.field("Bias", TSPBiasOption));
                        break;
                }
                break;
        }

        runButton.setOnAction(action -> {
            final int myVersion = this.version.get();
            runButton.setDisable(true);
            abortButton.setDisable(false);

            final AbstractPopulation work;

            // Thread options
            final boolean useUiLock = getSelected(waitForUi).equalsIgnoreCase("Yes");
            final boolean useParallel = getSelected(optimizationOption).equalsIgnoreCase("Parallel");
            final boolean useIslands = getSelected(optimizationOption).equalsIgnoreCase("Islands");
            int choosenThreads = getSelected(optimizationThreadCount).equalsIgnoreCase("Auto")
                    ? Runtime.getRuntime().availableProcessors()
                    : Integer.parseInt(getSelected(optimizationThreadCount));
            if (!(useIslands || useParallel)) choosenThreads = 1;

            // Pop/Gene size options
            final int pops = popOption.getNumberAsInt();
            final int genes = geneSizeOption.getNumberAsInt();

            // ACO options
            final String graphType = getSelected(graphOption);
            final int randomGraphSize = randomGraphOption.getNumberAsInt();
            final double percentChanging = percentOption.getNumberAsDouble();
            final String ACOMutation = getSelected(ACOMutationOption);

            // GA SS options
            final int goalChoice = goal.getNumberAsInt();
            int[] numArrayChoice = IntStream.range(1, SSNums.getNumberAsInt() + 1).toArray();
            final String selectionChoice = getSelected(selection);
            final String crossoverChoice = getSelected(crossover);
            final String mutationChoice = getSelected(mutation);

            var initialPopulation = new Wrap<AbstractPopulation>();
            Node.resetIdCounter();
            double[][] points = null;
            switch (algorithm.abbrivation) {
                case "GA":
                    switch (problem.abbrivation) {
                        case "OM":
                            if (oneMaxType.getSelectionModel().getSelectedItem().equalsIgnoreCase(OneMax.name))
                                initialPopulation.set(PopulationFactory.oneMax(genes, getSelected(OMBiasOption).equalsIgnoreCase("random")));
                            else
                                initialPopulation.set(PopulationFactory.leadingOnes(genes, getSelected(OMBiasOption).equalsIgnoreCase("random")));
                            break;
                        case "SS":
                            initialPopulation.set(new Population(
                                    pops, numArrayChoice.length, true,
                                    getSelected(SSBiasOption).equalsIgnoreCase("random"),
                                    useParallel ? choosenThreads : 1,
                                    Mutation.get(mutationChoice),
                                    Fitness.subsetSum(goalChoice, numArrayChoice),
                                    Crossover.get(crossoverChoice),
                                    Selection.get(selectionChoice),
                                    PreCalcs.get(crossoverChoice, selectionChoice, mutationChoice))
                            );
                            break;
                    }
                    break;
                case "ACO":
                    switch (problem.abbrivation) {
                        case "TSPP":
                            useCircle.set(false);
                        case "TSPC":
                            if (graphType.equalsIgnoreCase("random")) {
                                points = new double[randomGraphSize][];
                                Random random = new Random();
                                for (int i = 0; i < points.length; i++)
                                    points[i] = new double[]{random.nextInt((int) content.getWidth()), random.nextInt((int) content.getHeight())};
                            } else {
                                points = Pattern.compile("\n").splitAsStream(tspMaps.get(graphType))
                                        .map(i -> new double[]{Double.parseDouble(i.split(" ")[0]), Double.parseDouble(i.split(" ")[1])})
                                        .toArray(double[][]::new);
                            }

                            initialPopulation.set(ColonyFactory.travelingSalesman(points, pops, percentChanging, choosenThreads, NodeBias.get(getSelected(TSPBiasOption)), natural.ACO.Mutation.get(ACOMutation, useCircle.get())));
                            break;
                    }
                    break;
            }

            if (useIslands) {
                int islandConvergingPoint = islandConverging.getNumberAsInt();
                if (algorithm.abbrivation.equalsIgnoreCase("GA"))
                    work = IslandFactory.islandsOfPopulations((Population) initialPopulation.get(), choosenThreads, islandConvergingPoint);
                else
                    work = IslandFactory.islandsOfTSPColonies(
                            choosenThreads, islandConvergingPoint,
                            useCircle.get(),
                            points,
                            pops,
                            percentChanging,
                            choosenThreads,
                            NodeBias.get(getSelected(TSPBiasOption)),
                            natural.ACO.Mutation.get(ACOMutation, useCircle.get()));
                initialPopulation.set(((Islands) work).getIsland(0));
            } else {
                work = initialPopulation.get();
            }

            // Used to force the algorithm to wait for UI to finish using data before overwriting it
            var UILock = new Wrap<CountDownLatch>();

            /*
             * Algorithm thread task
             */
            Task task = new Task() {
                protected Object call() throws Exception {
                    var best = new Wrap<>(-1L);
                    // Action to do between each generation
                    Action action = () -> {
                            if (!version.isSame(myVersion))
                            throw new InterruptedException("Halting...");
                        if (initialPopulation.get().getBestFitness() != best.get()) {
                            best.set(initialPopulation.get().getBestFitness());
                            if (useUiLock)
                                UILock.set(new CountDownLatch(1));
                            updateMessage(best.toString());
                            if (useUiLock)
                                UILock.get().await(10, TimeUnit.SECONDS);
                        }
                    };
                    // Changes between different algorithm runs
                    switch (problem.abbrivation) {
                        case "SS":
                            if (useParallel) work.evolveUntilGoalParallel(Integer.MAX_VALUE, action);
                            else work.evolveUntilGoal(Integer.MAX_VALUE, action);
                            break;
                        case "OM":
                            if (useParallel) work.evolveUntilGoalParallel(genes, action);
                            else work.evolveUntilGoal(genes, action);
                            break;
                        default:
                            if (useParallel) work.evolveUntilNoProgressParallel(maxStagnation.getNumberAsInt(), action);
                            else work.evolveUntilNoProgress(maxStagnation.getNumberAsInt(), action);
                    }
                    abortButton.setDisable(true);
                    runButton.setDisable(false);
                    return null;
                }
            };

            /*
             * UI updater
             */
            task.messageProperty().addListener(((observable, oldValue, newValue) -> {
                switch (algorithm.abbrivation) {
                    case "GA":
                        switch (problem.abbrivation) {
                            case "OM":
                                updateOMGraph((Population) initialPopulation.get());
                                break;
                            case "SS":
                                updateSSGraph((Population) initialPopulation.get(), numArrayChoice);
                                break;
                        }
                        break;
                    case "ACO":
                        updateTSPGraph((Colony) initialPopulation.get(), useCircle.get());
                        break;
                }
                if (useUiLock)
                    UILock.get().countDown();
            }));

            /*
             * Initial setup of graph
             */
            switch (problem.abbrivation) {
                case "OM":
                case "SS":
                    drawTSPGraph(new Node[0]);
                    drawEdges();
                    break;
                case "TSPP":
                case "TSPC":
                    drawTSPGraph(((Colony) initialPopulation.get()).getGraph());
                    break;
            }

            new Thread(task).start();
        });

        if(algorithm.abbrivation.equalsIgnoreCase("p"))
            options.getChildren().addAll(benchButton);
        else
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

    private void drawEdges() {
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
        Edge[] path = colony.getEdges();
        graph.beginUpdate();
        model.getRemovedEdges().addAll(model.getAllEdges());
        for (Edge aPath : path)
            model.addEdge(aPath.source.name, aPath.target.name);

        long cost = Integer.MAX_VALUE - colony.getBestFitness();
        content.setBottom(new Label(String.valueOf(cost)));

        graph.endUpdate();
    }

    private void updateSSGraph(Population population, int[] nums) {
        updateOMGraph(
                getPos(population.getBestDna().toArray()),
                population.getBestDna().cardinality(),
                population.getBest().getLength());

        var dna = population.getBestDna();
        int sum = 0;
        for (int i = 0; i < dna.size(); i++) {
            if (dna.get(i))
                sum += nums[i];
        }
        content.setBottom(new Label(String.valueOf(sum)));
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
        double max = 0, min = 0,
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

