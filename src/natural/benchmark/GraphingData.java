package natural.benchmark;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;
import lsm.helpers.IO.read.text.TextReader;
import lsm.helpers.IO.write.text.TextWriter;
import lsm.helpers.IO.write.text.console.Note;
import lsm.helpers.Time;
import natural.ACO.Mutation;
import natural.ACO.NodeBias;
import natural.AbstractPopulation;
import natural.factory.ColonyFactory;
import natural.factory.PopulationFactory;

import java.util.Arrays;
import java.util.Random;
import java.util.regex.Pattern;

@SuppressWarnings({"WeakerAccess", "unused"})
public class GraphingData extends Application {
    private static String xAxis, yAxis;
    private static String[] lineNames;
    private static double[][][] lines;

    public static void init(String filename, String xAxis, String yAxis, Line[] formulas) throws Exception {
        GraphingData.xAxis = xAxis;
        GraphingData.yAxis = yAxis;
        double[][] data = TextReader.getTextReader(filename + ".txt").lines()
                .map(i -> Pattern.compile(" ").splitAsStream(i).mapToDouble(Double::parseDouble).toArray())
                .toArray(double[][]::new);

        lineNames = Arrays.stream(formulas).map(l -> l.name).toArray(String[]::new);
        lines = new double[formulas.length][][];
        for (var i = new Wrap<>(0); i.value < lines.length; i.value++)
            lines[i.value] = Arrays.stream(data).map(point -> formulas[i.value].lineFormula.convert(point)).toArray(double[][]::new);
        launch();
    }

    public void start(Stage stage) {
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
        Scene scene = new Scene(lineChart, 800, 600);
        stage.setScene(scene);
        stage.show();
    }

    private static String filename;

    public static void setFilename(String filename){
        GraphingData.filename = filename;
    }

    public static void display(String filename) throws Exception {
        GraphingData.filename = filename;
        switch (filename.substring(0, 2)) {
            case "OM":
                displayOM();
                break;
            case "LO":
                displayLO();
                break;
            case "TS":
                displayTSP();
        }
    }

    public static void generate(String filename) throws Exception {
        generate(filename, 1);
    }

    public static void generate(String filename, int times) throws Exception {
        GraphingData.filename = filename;
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
        }
    }

    public static void generate(String filename, int times, int step) throws Exception {
        GraphingData.filename = filename;
        Random random = new Random();
        var useCircle = new Wrap<>(false);
        switch (filename.substring(0, 2)) {
            case "OM":
                generate(PopulationFactory::oneMax,
                        (at, pop) -> pop.evolveUntilGoal(at),
                        AbstractPopulation::getGeneration,
                        Math.max(step, 1), step * 100, times);
                break;
            case "LO":
                generate(PopulationFactory::leadingOnes,
                        (at, pop) -> pop.evolveUntilGoal(at),
                        AbstractPopulation::getGeneration,
                        Math.max(step, 1), step * 100, times);
                break;
            case "TS":
                if(filename.substring(0, 4).equalsIgnoreCase("tspc"))
                    useCircle.value = true;
                generate(i -> {
                            double[][] points = new double[Math.max(i, 2)][];
                            for(int j = 0; j < points.length; j++)
                                points[j] = new double[]{random.nextInt(1000), random.nextInt(1000)};
                            return ColonyFactory.travelingSalesman(points, 1000, 0.05, Runtime.getRuntime().availableProcessors(), NodeBias.linearBias(), useCircle.value ? Mutation.twoOptCircle() : Mutation.twoOpt());
                        },
                        (at, pop) -> pop.evolveUntilNoProgressParallel(100),
                        (pop) -> pop.getGeneration() - 100,
                        step, Math.min(step * 100, 120), times);
                break;
        }
    }


    public static void displayLO() throws Exception {
        GraphingData.init(
                filename, "Genes", "Generations",
                new Line[]{
                        new Line("+10%", point -> new double[]{point[0],        /*1.1D / /**/0.86D * (point[0] * point[0]) * 1.1D}),
                        new Line("Expected", point -> new double[]{point[0],    /*1.1D / /**/0.86D * (point[0] * point[0]) * 1.0D}),
                        new Line("-10%", point -> new double[]{point[0],        /*1.1D / /**/0.86D * (point[0] * point[0]) * 0.9D}),
                        new Line("Data", point -> point)
                });
    }

    public static void displayOM() throws Exception {
        GraphingData.init(
                filename, "Genes", "Generations",
                new Line[]{
                        new Line("+10%", point -> new double[]{point[0],        Math.E * point[0] * Math.log(point[0]) * 1.1D}),
                        new Line("Expected", point -> new double[]{point[0],    Math.E * point[0] * Math.log(point[0]) * 1.0D}),
                        new Line("-10%", point -> new double[]{point[0],        Math.E * point[0] * Math.log(point[0]) * 0.9D}),
                        new Line("Data", point -> point)
                });
    }

    private static void displayTSP() throws Exception {
        GraphingData.init(
                filename, "Points", "Generations",
                new Line[]{
                        new Line("+10%", point -> new double[]{point[0],        point[0] * Math.log(point[0]) * 1.1D}),
                        new Line("Expected", point -> new double[]{point[0],    point[0] * Math.log(point[0]) * 1.0D}),
                        new Line("-10%", point -> new double[]{point[0],        point[0] * Math.log(point[0]) * 0.9D}),
                        new Line("Data", point -> point)
                });
    }

    private static void generate(PopulationCreator creator, Evolution evolution, Counter counter, int step, int max, int times) throws Exception {
        var writer = TextWriter.getWriter(filename, "txt", true);
        Time.init();
        for (var i = step; i <= max; i += step) {
            var generations = 0L;
            for (var j = 0; j < times; j++) {
                var population = creator.getPop(i);
                evolution.evolve(i, population);
                generations += counter.count(population);
            }
            writer.write(i + " " + ((double)generations / times) + "\n");
            Note.write(i).write(" -> ");
            Time.reset();
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
    int count(AbstractPopulation population);
}

class Wrap<T> {
    T value;
    Wrap(T value) {
        this.value = value;
    }
}

