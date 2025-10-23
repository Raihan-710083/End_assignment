package raihan.inholland.nl.end_assignment;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ScreenManager {
    private static ScreenManager instance;
    private Stage primaryStage;
    private final Map<String, Parent> screens = new HashMap<>();
    private final Map<String, Object> controllers = new HashMap<>();

    private ScreenManager() {}

    public static ScreenManager getInstance() {
        if (instance == null) {
            instance = new ScreenManager();
        }
        return instance;
    }

    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
    }

    public void loadScreen(String name, String fxmlPath) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent screen = loader.load();
        screens.put(name, screen);
        controllers.put(name, loader.getController());
    }

    public void showScreen(String name) {
        if (screens.containsKey(name)) {
            Scene scene = new Scene(screens.get(name), 800, 600);
            primaryStage.setScene(scene);
            primaryStage.show();
        }
    }

    public Object getController(String name) {
        return controllers.get(name);
    }
}