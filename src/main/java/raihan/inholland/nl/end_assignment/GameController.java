package raihan.inholland.nl.end_assignment;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.List;

public class GameController {
    private static final int MIN_TIME_LIMIT = 5;
    private static final int MAX_TIME_LIMIT = 300;
    private static final double LOW_TIME_THRESHOLD = 0.2;
    private static final double CRITICAL_TIME_THRESHOLD = 0.1;

    @FXML private Label questionTitle;
    @FXML private Label timerLabel;
    @FXML private VBox answersContainer;
    @FXML private Button submitButton;
    @FXML private Label playerNameLabel;
    @FXML private ProgressBar timerProgressBar;

    private Quiz quiz;
    private int currentQuestionIndex;
    private String playerName;
    private List<String> userAnswers;
    private Timeline timer;
    private int timeRemaining;
    private int totalTimeForQuestion;

    public GameController() {
        this.currentQuestionIndex = 0;
        this.userAnswers = new ArrayList<>();
    }

    public void setQuiz(Quiz quiz) {
        validateQuiz(quiz);
        this.quiz = quiz;
        resetGameState();
        showPlayerNameInput();
    }

    private void validateQuiz(Quiz quiz) {
        if (quiz == null) {
            throw new IllegalArgumentException("Quiz cannot be null");
        }
        if (quiz.getPages() == null || quiz.getPages().isEmpty()) {
            throw new IllegalArgumentException("Quiz must contain at least one page");
        }
    }

    private void resetGameState() {
        currentQuestionIndex = 0;
        userAnswers.clear();
        stopTimer();
    }

    private void showPlayerNameInput() {
        TextInputDialog dialog = createPlayerNameDialog();
        dialog.showAndWait().ifPresent(this::initializePlayer);
    }

    private TextInputDialog createPlayerNameDialog() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Player Name");
        dialog.setHeaderText("Enter your name");
        dialog.setContentText("Name:");
        return dialog;
    }

    private void initializePlayer(String name) {
        if (name == null || name.trim().isEmpty()) {
            showAlert("Invalid Input", "Player name cannot be empty");
            showPlayerNameInput();
            return;
        }
        this.playerName = name.trim();
        playerNameLabel.setText("Player: " + this.playerName);
        showQuestion();
    }

    private void showQuestion() {
        stopTimer();

        if (isQuizCompleted()) {
            showResults();
            return;
        }

        try {
            Page currentPage = getCurrentPage();
            Question question = getCurrentQuestion(currentPage);
            displayQuestion(question);
            startTimer(currentPage.getTimeLimit());
        } catch (IllegalStateException e) {
            handleQuestionError(e);
        }
    }

    private boolean isQuizCompleted() {
        return currentQuestionIndex >= quiz.getPages().size();
    }

    private Page getCurrentPage() {
        return quiz.getPages().get(currentQuestionIndex);
    }

    private Question getCurrentQuestion(Page page) {
        if (page.getElements() == null || page.getElements().isEmpty()) {
            throw new IllegalStateException("No questions found in page " + (currentQuestionIndex + 1));
        }
        return page.getElements().get(0);
    }

    private void displayQuestion(Question question) {
        questionTitle.setText(formatQuestionTitle());
        answersContainer.getChildren().clear();

        if (question instanceof MultipleChoiceQuestion) {
            displayMultipleChoiceQuestion((MultipleChoiceQuestion) question);
        } else if (question instanceof BooleanQuestion) {
            displayBooleanQuestion((BooleanQuestion) question);
        } else {
            throw new IllegalStateException("Unsupported question type: " + question.getType());
        }
    }

    private String formatQuestionTitle() {
        return String.format("Question %d: %s", currentQuestionIndex + 1,
                getCurrentPage().getElements().get(0).getTitle());
    }

    private void displayMultipleChoiceQuestion(MultipleChoiceQuestion question) {
        ToggleGroup choiceGroup = new ToggleGroup();
        for (String choice : question.getChoices()) {
            RadioButton radioButton = createChoiceRadioButton(choice, choiceGroup);
            answersContainer.getChildren().add(radioButton);
        }
    }

    private void displayBooleanQuestion(BooleanQuestion question) {
        ToggleGroup booleanGroup = new ToggleGroup();
        RadioButton trueButton = createBooleanRadioButton(question.getLabelTrue(), "true", booleanGroup);
        RadioButton falseButton = createBooleanRadioButton(question.getLabelFalse(), "false", booleanGroup);
        answersContainer.getChildren().addAll(trueButton, falseButton);
    }

    private RadioButton createChoiceRadioButton(String choice, ToggleGroup group) {
        RadioButton radioButton = new RadioButton(choice);
        radioButton.setToggleGroup(group);
        radioButton.setUserData(choice);
        return radioButton;
    }

    private RadioButton createBooleanRadioButton(String label, String value, ToggleGroup group) {
        RadioButton radioButton = new RadioButton(label);
        radioButton.setToggleGroup(group);
        radioButton.setUserData(value);
        return radioButton;
    }

    private void startTimer(int timeLimit) {
        validateTimeLimit(timeLimit);
        initializeTimer(timeLimit);
        startTimerExecution();
    }

    private void validateTimeLimit(int timeLimit) {
        if (timeLimit < MIN_TIME_LIMIT || timeLimit > MAX_TIME_LIMIT) {
            throw new IllegalArgumentException(
                    String.format("Time limit must be between %d and %d seconds", MIN_TIME_LIMIT, MAX_TIME_LIMIT)
            );
        }
    }

    private void initializeTimer(int timeLimit) {
        this.timeRemaining = timeLimit;
        this.totalTimeForQuestion = timeLimit;
        updateTimerDisplay();

        this.timer = new Timeline(
                new KeyFrame(Duration.seconds(1), e -> handleTimerTick())
        );
        this.timer.setCycleCount(Timeline.INDEFINITE);
    }

    private void startTimerExecution() {
        if (timer != null) {
            timer.play();
        }
    }

    private void handleTimerTick() {
        timeRemaining--;
        updateTimerDisplay();

        if (timeRemaining <= 0) {
            handleTimeUp();
        }
    }

    private void updateTimerDisplay() {
        timerLabel.setText(String.format("Time: %ds", timeRemaining));
        updateProgressBar();
        updateTimerColor();
    }

    private void updateProgressBar() {
        if (totalTimeForQuestion > 0) {
            double progress = (double) timeRemaining / totalTimeForQuestion;
            timerProgressBar.setProgress(progress);
        }
    }

    private void updateTimerColor() {
        double progressRatio = (double) timeRemaining / totalTimeForQuestion;

        if (progressRatio <= CRITICAL_TIME_THRESHOLD) {
            timerLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
        } else if (progressRatio <= LOW_TIME_THRESHOLD) {
            timerLabel.setStyle("-fx-text-fill: orange; -fx-font-weight: bold;");
        } else {
            timerLabel.setStyle("-fx-text-fill: black;");
        }
    }

    private void handleTimeUp() {
        stopTimer();
        userAnswers.add(""); // Empty answer for timeout
        currentQuestionIndex++;
        showTimeUpAlert();
        showQuestion();
    }

    private void showTimeUpAlert() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Time's Up!");
        alert.setHeaderText(null);
        alert.setContentText("Time's up for this question! Moving to next question.");
        alert.showAndWait();
    }

    private void stopTimer() {
        if (timer != null) {
            timer.stop();
            timer = null;
        }
    }

    @FXML
    private void handleSubmitAnswer() {
        stopTimer();

        try {
            String selectedAnswer = getSelectedAnswer();
            if (selectedAnswer != null) {
                processAnswer(selectedAnswer);
            } else {
                handleNoAnswerSelected();
            }
        } catch (Exception e) {
            handleSubmissionError(e);
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

    private void processAnswer(String answer) {
        userAnswers.add(answer);
        currentQuestionIndex++;
        showQuestion();
    }

    private void handleNoAnswerSelected() {
        showAlert("No Answer Selected", "Please select an answer before submitting.");
        restartTimerIfNeeded();
    }

    private void restartTimerIfNeeded() {
        if (currentQuestionIndex < quiz.getPages().size()) {
            Page currentPage = quiz.getPages().get(currentQuestionIndex);
            startTimer(timeRemaining);
        }
    }

    private void handleQuestionError(IllegalStateException e) {
        showAlert("Question Error", e.getMessage());
        currentQuestionIndex++;
        showQuestion();
    }

    private void handleSubmissionError(Exception e) {
        showAlert("Submission Error", "An error occurred while submitting your answer: " + e.getMessage());
    }

    private void showResults() {
        stopTimer();
        int score = calculateScore();
        displayResults(score);
    }

    private int calculateScore() {
        int score = 0;
        for (int i = 0; i < quiz.getPages().size(); i++) {
            if (i < userAnswers.size() && isAnswerCorrect(i)) {
                score++;
            }
        }
        return score;
    }

    private boolean isAnswerCorrect(int questionIndex) {
        String userAnswer = userAnswers.get(questionIndex);
        if (userAnswer == null || userAnswer.isEmpty()) {
            return false;
        }

        Question question = quiz.getPages().get(questionIndex).getElements().get(0);
        return question.isCorrect(userAnswer);
    }

    private void displayResults(int score) {
        try {
            ResultsController resultsController = getResultsController();
            resultsController.setResults(playerName, score, quiz.getPages().size());
            ScreenManager.getInstance().showScreen("results");
        } catch (Exception e) {
            showAlert("Results Error", "Cannot display results: " + e.getMessage());
        }
    }

    private ResultsController getResultsController() {
        Object controller = ScreenManager.getInstance().getController("results");
        if (!(controller instanceof ResultsController)) {
            throw new IllegalStateException("Results controller not found or wrong type");
        }
        return (ResultsController) controller;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}