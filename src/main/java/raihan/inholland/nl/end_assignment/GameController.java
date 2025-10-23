package raihan.inholland.nl.end_assignment;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import java.util.ArrayList;
import java.util.List;

public class GameController {
    @FXML private Label questionTitle;
    @FXML private Label timerLabel;
    @FXML private VBox answersContainer;
    @FXML private Button submitButton;
    @FXML private Label playerNameLabel;

    private Quiz quiz;
    private int currentQuestionIndex = 0;
    private String playerName;
    private List<String> userAnswers = new ArrayList<>();

    public void setQuiz(Quiz quiz) {
        this.quiz = quiz;
        this.currentQuestionIndex = 0;
        this.userAnswers.clear();

        // Safety check
        if (quiz == null || quiz.getPages() == null || quiz.getPages().isEmpty()) {
            showAlert("No valid quiz data!");
            return;
        }

        showPlayerNameInput();
    }

    private void showPlayerNameInput() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Player Name");
        dialog.setHeaderText("Enter your name");
        dialog.setContentText("Name:");

        dialog.showAndWait().ifPresent(name -> {
            this.playerName = name;
            playerNameLabel.setText("Player: " + name);
            showQuestion();
        });
    }

    private void showQuestion() {
        // Safety checks
        if (quiz == null || quiz.getPages() == null || quiz.getPages().isEmpty()) {
            showAlert("No quiz data available!");
            return;
        }

        if (currentQuestionIndex < quiz.getPages().size()) {
            Page currentPage = quiz.getPages().get(currentQuestionIndex);

            // Safety check for elements
            if (currentPage.getElements() == null || currentPage.getElements().isEmpty()) {
                showAlert("No questions found in page " + (currentQuestionIndex + 1));
                currentQuestionIndex++;
                showQuestion(); // Skip to next question
                return;
            }

            Question question = currentPage.getElements().get(0); // Assuming one question per page

            questionTitle.setText(question.getTitle());
            answersContainer.getChildren().clear();

            if (question instanceof MultipleChoiceQuestion) {
                showMultipleChoiceQuestion((MultipleChoiceQuestion) question);
            } else if (question instanceof BooleanQuestion) {
                showBooleanQuestion((BooleanQuestion) question);
            }

            startTimer(currentPage.getTimeLimit());
        } else {
            showResults();
        }
    }

    // ... rest of your methods remain the same ...
    private void showMultipleChoiceQuestion(MultipleChoiceQuestion question) {
        ToggleGroup group = new ToggleGroup();
        for (String choice : question.getChoices()) {
            RadioButton radioButton = new RadioButton(choice);
            radioButton.setToggleGroup(group);
            radioButton.setUserData(choice);
            answersContainer.getChildren().add(radioButton);
        }
    }

    private void showBooleanQuestion(BooleanQuestion question) {
        ToggleGroup group = new ToggleGroup();
        RadioButton trueButton = new RadioButton(question.getLabelTrue());
        RadioButton falseButton = new RadioButton(question.getLabelFalse());
        trueButton.setToggleGroup(group);
        falseButton.setToggleGroup(group);
        trueButton.setUserData("true");
        falseButton.setUserData("false");
        answersContainer.getChildren().addAll(trueButton, falseButton);
    }

    private void startTimer(int seconds) {
        // Timer implementation will be added in Part 3
        timerLabel.setText("Time: " + seconds + "s");
    }

    @FXML
    private void handleSubmitAnswer() {
        String answer = getSelectedAnswer();
        if (answer != null) {
            userAnswers.add(answer);
            currentQuestionIndex++;
            showQuestion();
        } else {
            showAlert("Please select an answer!");
        }
    }

    private String getSelectedAnswer() {
        for (var child : answersContainer.getChildren()) {
            if (child instanceof RadioButton) {
                RadioButton radio = (RadioButton) child;
                if (radio.isSelected()) {
                    return radio.getUserData().toString();
                }
            }
        }
        return null;
    }

    private void showResults() {
        int score = calculateScore();
        ResultsController resultsController = (ResultsController) ScreenManager.getInstance().getController("results");
        resultsController.setResults(playerName, score, quiz.getPages().size());
        ScreenManager.getInstance().showScreen("results");
    }

    private int calculateScore() {
        int score = 0;
        for (int i = 0; i < quiz.getPages().size(); i++) {
            if (i < userAnswers.size()) {
                Question question = quiz.getPages().get(i).getElements().get(0);
                if (question.isCorrect(userAnswers.get(i))) {
                    score++;
                }
            }
        }
        return score;
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}