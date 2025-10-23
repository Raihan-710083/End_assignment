package raihan.inholland.nl.end_assignment;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import java.io.File;
import java.io.IOException;

public class MenuController {
    private static final String JSON_EXTENSION = "*.json";

    @FXML private Button loadQuizButton;
    @FXML private Button startQuizButton;
    @FXML private Label statusLabel;

    private Quiz currentQuiz;

    @FXML
    private void initialize() {
        startQuizButton.setDisable(true);
    }

    @FXML
    private void handleLoadQuiz() {
        try {
            File selectedFile = showFileChooser();
            if (selectedFile != null) {
                loadQuizFromFile(selectedFile);
            }
        } catch (Exception e) {
            handleLoadError(e);
        }
    }

    private File showFileChooser() {
        FileChooser fileChooser = createFileChooser();
        return fileChooser.showOpenDialog(null);
    }

    private FileChooser createFileChooser() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Quiz JSON File");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("JSON files", JSON_EXTENSION)
        );
        return fileChooser;
    }

    private void loadQuizFromFile(File file) throws IOException {
        currentQuiz = SimpleJsonParser.parseQuizFromFile(file);
        validateLoadedQuiz();
        updateUIAfterLoad();
    }

    private void validateLoadedQuiz() {
        if (currentQuiz == null) {
            throw new IllegalStateException("Failed to load quiz: parser returned null");
        }
        if (currentQuiz.getPages() == null || currentQuiz.getPages().isEmpty()) {
            throw new IllegalStateException("Loaded quiz contains no pages");
        }
    }

    private void updateUIAfterLoad() {
        statusLabel.setText("Quiz loaded: " + currentQuiz.getTitle());
        startQuizButton.setDisable(false);
        logQuizDetails();
    }

    private void logQuizDetails() {
        System.out.println("Quiz loaded: " + currentQuiz.getTitle());
        System.out.println("Number of pages: " + currentQuiz.getPages().size());
        if (!currentQuiz.getPages().isEmpty()) {
            System.out.println("First page time limit: " + currentQuiz.getPages().get(0).getTimeLimit());
            System.out.println("First page elements: " + currentQuiz.getPages().get(0).getElements().size());
        }
    }

    private void handleLoadError(Exception e) {
        statusLabel.setText("Error loading quiz: " + e.getMessage());
        startQuizButton.setDisable(true);
        e.printStackTrace();
    }

    @FXML
    private void handleStartQuiz() {
        if (isQuizValid()) {
            startQuizGame();
        } else {
            statusLabel.setText("No valid quiz loaded!");
        }
    }

    private boolean isQuizValid() {
        return currentQuiz != null &&
                currentQuiz.getPages() != null &&
                !currentQuiz.getPages().isEmpty();
    }

    private void startQuizGame() {
        try {
            GameController gameController = getGameController();
            gameController.setQuiz(currentQuiz);
            ScreenManager.getInstance().showScreen("game");
        } catch (Exception e) {
            statusLabel.setText("Error starting quiz: " + e.getMessage());
        }
    }

    private GameController getGameController() {
        Object controller = ScreenManager.getInstance().getController("game");
        if (!(controller instanceof GameController)) {
            throw new IllegalStateException("Game controller not found or wrong type");
        }
        return (GameController) controller;
    }
}