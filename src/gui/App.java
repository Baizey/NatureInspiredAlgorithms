package gui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lsm.helpers.IO.write.text.console.Note;

import java.util.ArrayList;
import java.util.Arrays;
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
        Scene scene = new Scene(new VBox(), 1280, 720);
        ((VBox) scene.getRoot()).getChildren().addAll(makeMenuBar());
        primaryStage.setTitle("Nature Based Algorithm Visualization");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private MenuBar makeMenuBar() {
        var result = new MenuBar();
        ArrayList<Menu> menuOptions = Arrays.stream(algorithms)
                .map(algorithm -> {
                    Menu menu = new Menu(algorithm.name);
                    for (Problem problem : algorithm.problems) {
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
}
