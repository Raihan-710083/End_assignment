package raihan.inholland.nl.end_assignment;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class ResultsController {
    @FXML private Label scoreLabel;
    @FXML private Label playerNameLabel;

    public void setResults(String playerName, int score, int totalQuestions) {
        playerNameLabel.setText("Player: " + playerName);
        scoreLabel.setText("Score: " + score + "/" + totalQuestions);
    }

    @FXML
    private void handleBackToMenu() {
        ScreenManager.getInstance().showScreen("menu");
    }
}