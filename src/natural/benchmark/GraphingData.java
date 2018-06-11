package natural.benchmark;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;
import lsm.helpers.IO.read.text.TextReader;
import lsm.helpers.IO.write.text.TextWriter;
import lsm.helpers.Time;
import lsm.helpers.utils.Wrap;
import natural.ACO.Ant;
import natural.ACO.AntColonyMutations;
import natural.ACO.NodeBias;
import natural.AbstractPopulation;
import natural.factory.ColonyFactory;
import natural.factory.PopulationFactory;

import java.util.Arrays;
import java.util.Random;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

public class GraphingData extends Application {
    private static String[] files;
    private static String xAxis, yAxis;
    private static String[] lineNames;
    private static double[][][] lines;

    public static void init(String filename, String xAxis, String yAxis, Line[] formulas) throws Exception {
        init(new String[]{filename}, xAxis, yAxis, formulas);
    }
    public static void init(String[] files, String xAxis, String yAxis, Line[] formulas) throws Exception {
        GraphingData.xAxis = xAxis;
        GraphingData.yAxis = yAxis;

        double[][][] points = new double[files.length][][];
        lines = new double[formulas.length][][];
        for(int j = 0; j < files.length; j++) {
            String file = files[j];
            points[j] = TextReader.getTextReader(file + ".txt").lines()
                    .map(i -> Pattern.compile(" ").splitAsStream(i).mapToDouble(Double::parseDouble).toArray())
                    .toArray(double[][]::new);
        }
        lineNames = Arrays.stream(formulas).map(l -> l.name).toArray(String[]::new);
        for (var i = 0; i < lines.length; i++)
            lines[i] = formulas[i].lineFormula.convert(points);
    }

    public static void launchDisplay(String filename) throws Exception {
        display(filename);
        launch();
    }

    public static void launchDisplay(String[] filename) throws Exception {
        display(filename);
        launch();
    }

    public static LineChart getDisplay(String filename) throws Exception {
        display(filename);
        return getDisplay();
    }
    public static LineChart getDisplay(String[] filename) throws Exception {
        display(filename);
        return getDisplay();
    }

    public static LineChart getDisplay() {
        final var xAxis = new NumberAxis();
        xAxis.setLabel(GraphingData.xAxis);
        final var yAxis = new NumberAxis();
        yAxis.setLabel(GraphingData.yAxis);
        final var lineChart = new LineChart<>(xAxis, yAxis);

        for (int i = 0; i < lines.length; i++) {
            var series = new XYChart.Series();
            series.setName(lineNames[i]);
            for (double[] point : lines[i])
                series.getData().add(new XYChart.Data<>(point[0], point[1]));
            lineChart.getData().add(series);
        }
        lineChart.setCreateSymbols(false);
        lineChart.setAnimated(false);
        return lineChart;
    }

    public void start(Stage stage) {
        Scene scene = new Scene(getDisplay(), 800, 600);
        stage.setScene(scene);
        stage.show();
    }
    public static void setFiles(String[] files) {
        GraphingData.files = files;
    }
    public static void setFiles(String filename) {
        setFiles(new String[]{filename});
    }

    public static void display(String[] files) throws Exception {
        setFiles(files);
        switch (files[0].substring(0, 2)) {
            case "OM":
                displayOM();
                break;
            case "LO":
                displayLO();
                break;
            case "SS":
                displaySS();
                //displayData(files, "Set size", "Generations");
                break;
            case "TS":
                if(files[0].substring(0, 4).equalsIgnoreCase("tspc"))
                    displayTSPC();
                else
                    displayTSPP();
                break;
            default:
                if(files[0].charAt(files[0].length() - 1) == '2')
                    displayData(files, "Cities", "Seconds");
                else
                    displayData(files, "Generations", "Seconds");
                break;
        }
    }
    public static void display(String filename) throws Exception {
        setFiles(filename);
    }

    public static void generate(String filename) throws Exception {
        generate(filename, 1);
    }

    public static void generate(String filename, int times) throws Exception {
        setFiles(filename);
        switch (filename.substring(0, 2)) {
            case "OM":
                generate(filename, times, 1000);
                break;
            case "LO":
                generate(filename, times, 100);
                break;
            case "TS":
                generate(filename, times, 1);
                break;
            case "SS":
                generate(filename, times, 100);
                break;
            default:
                generate(filename, times, 1);
        }
    }

    public static void generate(String filename, int times, int step) throws Exception {
        setFiles(filename);
        Random random = new Random();
        var useCircle = new Wrap<>(false);
        switch (filename.substring(0, 2).toUpperCase()) {
            case "OM":
                generate(genes -> PopulationFactory.oneMax(genes, true),
                        (at, pop) -> pop.evolveUntilGoal(at),
                        AbstractPopulation::getGeneration,
                        step, step, step * 100, times);
                break;
            case "LO":
                generate(genes -> PopulationFactory.leadingOnes(genes, true),
                        (at, pop) -> pop.evolveUntilGoal(at),
                        AbstractPopulation::getGeneration,
                        step, step, step * 100, times);
                break;
            case "SS":
                generate(genes -> PopulationFactory.subsetSum(genes, genes * genes / 2, true),
                        (genes, pop) -> pop.evolveUntilGoal(IntStream.range(1, pop.getBest().getLength() + 1).sum()),
                        AbstractPopulation::getGeneration,
                        step, step, step * 100, times);
                break;
            case "TS":
                if (filename.substring(0, 4).equalsIgnoreCase("tspc")) useCircle.set(true);
                generate(i -> {
                            double[][] points = new double[Math.max(i, 2)][];
                            for (int j = 0; j < points.length; j++)
                                points[j] = new double[]{random.nextInt(1000), random.nextInt(1000)};
                            return ColonyFactory.travelingSalesman(points, 1000, 0D, 1D, 0.01, Runtime.getRuntime().availableProcessors(), NodeBias.polynomialBias(), useCircle.get() ? AntColonyMutations.twoOptCircle() : AntColonyMutations.twoOpt());
                        },
                        (at, pop) -> pop.evolveUntilNoProgressParallel(100),
                        (pop) -> (long) Arrays.stream(((Ant) pop.getBest()).getEdges()).mapToDouble(i -> i.cost).sum(),
                        Math.max(5, step), step, Math.min(step * 100, 120), times);
                break;
            case "NO": // No parallel
                generate(i -> {
                            double[][] points = new double[Math.max(i, 2)][];
                            for (int j = 0; j < points.length; j++)
                                points[j] = new double[]{random.nextInt(1000), random.nextInt(1000)};
                            Time.using(Time.SECONDS);
                            var colony = ColonyFactory.travelingSalesman(points, 1000, 0D, 1D, 0.01, Runtime.getRuntime().availableProcessors(), NodeBias.polynomialBias(), useCircle.get() ? AntColonyMutations.twoOptCircle() : AntColonyMutations.twoOpt());
                            Time.init("NoParallel");
                            return colony;
                        },
                        (at, pop) -> pop.evolve(100),
                        (pop) -> Time.get("NoParallel"),
                        Math.max(5, step), step, Math.min(120, step * 100), times);
                break;
            case "US": // Use parallel
                generate(i -> {
                            double[][] points = new double[Math.max(i, 2)][];
                            for (int j = 0; j < points.length; j++)
                                points[j] = new double[]{random.nextInt(1000), random.nextInt(1000)};
                            Time.using(Time.SECONDS);
                            var colony = ColonyFactory.travelingSalesman(points, 1000, 0D, 1D, 0.01, Runtime.getRuntime().availableProcessors(), NodeBias.polynomialBias(), useCircle.get() ? AntColonyMutations.twoOptCircle() : AntColonyMutations.twoOpt());
                            Time.init("UseParallel");
                            return colony;
                        },
                        (at, pop) -> pop.evolveParallel(100),
                        (pop) -> Time.get("UseParallel"),
                        Math.max(5, step), step, Math.min(120, step * 100), times);
                break;
        }
    }


    public static void displayData(int count, String[] names) throws Exception {
        displayData(names, "Points", "Time");
    }

    public static void displayData(String[] names, String xAxis, String yAxis) throws Exception {
        // TODO: fix temporary fix of removing numbers from filenames for labels
        GraphingData.init(
                files, xAxis, yAxis,
                IntStream.range(0, names.length)
                        .mapToObj(i -> new Line(names[i].replaceAll("\\d+", ""), points -> Arrays.stream(points[i]).toArray(double[][]::new)))
                        .toArray(Line[]::new));
    }

    public static void displayLO() throws Exception {
        GraphingData.init(
                files, "Genes", "Generations",
                new Line[]{
                        new Line("+10%", points -> Arrays.stream(points[0]).map(point -> new double[]{
                                point[0],
                                0.86D * (point[0] * point[0]) * 1.1D
                        }).toArray(double[][]::new)),
                        new Line("Expected", points -> Arrays.stream(points[0]).map(point -> new double[]{
                                point[0],
                                0.86D * (point[0] * point[0]) * 1.0D
                        }).toArray(double[][]::new)),
                        new Line("-10%", points -> Arrays.stream(points[0]).map(point -> new double[]{
                                point[0],
                                0.86D * (point[0] * point[0]) * 0.9D
                        }).toArray(double[][]::new)),
                        new Line("Data", points -> Arrays.stream(points[0]).toArray(double[][]::new)),
                });
    }

    public static void displayOM() throws Exception {
        GraphingData.init(
                files, "Genes", "Generations",
                new Line[]{
                        new Line("+10%", points -> Arrays.stream(points[0]).map(point -> new double[]{
                                point[0],
                                Math.E * point[0] * Math.log(point[0]) * 1.1D
                        }).toArray(double[][]::new)),
                        new Line("Expected", points -> Arrays.stream(points[0]).map(point -> new double[]{
                                point[0],
                                Math.E * point[0] * Math.log(point[0]) * 1.0D
                        }).toArray(double[][]::new)),
                        new Line("-10%", points -> Arrays.stream(points[0]).map(point -> new double[]{
                                point[0],
                                Math.E * point[0] * Math.log(point[0]) * 0.9D
                        }).toArray(double[][]::new)),
                        new Line("Data", points -> Arrays.stream(points[0]).toArray(double[][]::new)),
                });
    }
    public static void displaySS() throws Exception {
        GraphingData.init(
                files, "Genes", "Generations",
                new Line[]{
                        new Line("+10%", points -> Arrays.stream(points[0]).map(point -> new double[]{
                                point[0],
                                Math.E * point[0] * Math.log(point[0]) * 1.1D
                        }).toArray(double[][]::new)),
                        new Line("Expected", points -> Arrays.stream(points[0]).map(point -> new double[]{
                                point[0],
                                Math.E * point[0] * Math.log(point[0]) * 1.0D
                        }).toArray(double[][]::new)),
                        new Line("-10%", points -> Arrays.stream(points[0]).map(point -> new double[]{
                                point[0],
                                Math.E * point[0] * Math.log(point[0]) * 0.9D
                        }).toArray(double[][]::new)),
                        new Line("Data", points -> Arrays.stream(points[0]).toArray(double[][]::new)),
                });
    }


    private static void displayTSPC() throws Exception {
        GraphingData.init(
                files, "Points", "Path distance",
                new Line[]{
                        new Line("Upper bound", points -> Arrays.stream(points[0]).map(point -> new double[]{
                                point[0],
                                1000D * (Math.sqrt(2D * point[0]) + 1.75)
                        }).toArray(double[][]::new)),
                        new Line("Average", points -> Arrays.stream(points[0]).map(point -> new double[]{
                                point[0],
                                1000D * (((Math.sqrt(2D * point[0]) + 1.75) + .7078D * Math.sqrt(point[0]) + .551D) / 2D)
                        }).toArray(double[][]::new)),
                        new Line("Lower bound", points -> Arrays.stream(points[0]).map(point -> new double[]{
                                point[0],
                                1000D * (.7078D * Math.sqrt(point[0]) + .551D)
                        }).toArray(double[][]::new)),
                        new Line("Data", points -> Arrays.stream(points[0]).toArray(double[][]::new)),
                });
    }

    private static void displayTSPP() throws Exception {
        GraphingData.init(
                files, "Points", "Path distance",
                new Line[]{
                        new Line("Upper bound", points -> Arrays.stream(points[0]).map(point -> new double[]{
                                point[0] + 1,
                                1000D * (Math.sqrt(2D * point[0]) + 1.75)
                        }).toArray(double[][]::new)),
                        new Line("Average", points -> Arrays.stream(points[0]).map(point -> new double[]{
                                point[0] + 1,
                                1000D * (((Math.sqrt(2D * point[0]) + 1.75) + .7078D * Math.sqrt(point[0]) + .551D) / 2D)
                        }).toArray(double[][]::new)),
                        new Line("Lower bound", points -> Arrays.stream(points[0]).map(point -> new double[]{
                                point[0] + 1,
                                1000D * (.7078D * Math.sqrt(point[0]) + .551D)
                        }).toArray(double[][]::new)),
                        new Line("Data", points -> Arrays.stream(points[0]).toArray(double[][]::new)),
                });
    }

    private static void generate(PopulationCreator creator, Evolution evolution, Counter counter, int start, int step, int max, int times) throws Exception {
        var writer = TextWriter.getWriter(files[0], "txt", true);
        Time.init("Program " + start);
        for (var i = start; i <= max; i += step) {
            var generations = 0D;
            for (var j = 0; j < times; j++) {
                var population = creator.getPop(i);
                evolution.evolve(i, population);
                generations += counter.count(population);
            }
            writer.write(i + " " + (generations / times) + "\n");
            Time.write("Program " + i);
            Time.init("Program " + (i + step));
            writer.flush();
        }
        writer.flush();
        writer.close();
    }

}

interface PopulationCreator {
    AbstractPopulation getPop(int at);
}

interface Evolution {
    void evolve(int at, AbstractPopulation population) throws Exception;
}

interface Counter {
    double count(AbstractPopulation population);
}

