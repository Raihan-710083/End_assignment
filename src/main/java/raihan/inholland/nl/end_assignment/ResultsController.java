package raihan.inholland.nl.end_assignment;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class ResultsController {
    @FXML private Label scoreLabel;
    @FXML private Label playerNameLabel;
    @FXML private Label performanceLabel;

    public void setResults(String playerName, int score, int totalQuestions) {
        playerNameLabel.setText("Player: " + playerName);
        scoreLabel.setText("Score: " + score + "/" + totalQuestions);

        // Add performance feedback based on score
        double percentage = (double) score / totalQuestions * 100;
        String performance;

        if (percentage >= 80) {
            performance = "Excellent! 🎉";
        } else if (percentage >= 60) {
            performance = "Good job! 👍";
        } else if (percentage >= 40) {
            performance = "Not bad! 🙂";
        } else {
            performance = "Keep practicing! 💪";
        }

        performanceLabel.setText(performance);
    }

    @FXML
    private void handleBackToMenu() {
        ScreenManager.getInstance().showScreen("menu");
    }
}