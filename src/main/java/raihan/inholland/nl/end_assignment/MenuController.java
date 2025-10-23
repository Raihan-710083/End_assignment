package raihan.inholland.nl.end_assignment;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import java.io.File;

public class MenuController {
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
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Quiz JSON File");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("JSON files", "*.json")
        );

        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            try {
                currentQuiz = SimpleJsonParser.parseQuizFromFile(file);

                // Debug output
                System.out.println("Quiz loaded: " + currentQuiz.getTitle());
                System.out.println("Number of pages: " + currentQuiz.getPages().size());
                if (!currentQuiz.getPages().isEmpty()) {
                    System.out.println("First page time limit: " + currentQuiz.getPages().get(0).getTimeLimit());
                    System.out.println("First page elements: " + currentQuiz.getPages().get(0).getElements().size());
                }

                statusLabel.setText("Quiz loaded: " + currentQuiz.getTitle());
                startQuizButton.setDisable(false);
            } catch (Exception e) {
                statusLabel.setText("Error loading quiz: " + e.getMessage());
                e.printStackTrace(); // Add this to see the full error
                startQuizButton.setDisable(true);
            }
        }
    }

    @FXML
    private void handleStartQuiz() {
        if (currentQuiz != null && !currentQuiz.getPages().isEmpty()) {
            GameController gameController = (GameController) ScreenManager.getInstance().getController("game");
            gameController.setQuiz(currentQuiz);
            ScreenManager.getInstance().showScreen("game");
        } else {
            statusLabel.setText("No valid quiz loaded!");
        }
    }
}