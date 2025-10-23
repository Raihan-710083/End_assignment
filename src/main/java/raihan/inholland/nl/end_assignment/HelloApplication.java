package raihan.inholland.nl.end_assignment;

import javafx.application.Application;
import javafx.stage.Stage;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        ScreenManager screenManager = ScreenManager.getInstance();
        screenManager.setPrimaryStage(stage);

        // Load all screens
        screenManager.loadScreen("menu", "menu-view.fxml");
        screenManager.loadScreen("game", "game-view.fxml");
        screenManager.loadScreen("results", "results-view.fxml");

        // Show menu screen first
        screenManager.showScreen("menu");
    }

    public static void main(String[] args) {
        launch(args);
    }
}