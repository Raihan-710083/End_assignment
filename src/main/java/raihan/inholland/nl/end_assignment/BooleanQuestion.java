package raihan.inholland.nl.end_assignment;

public class BooleanQuestion extends Question {
    private String labelTrue;
    private String labelFalse;
    private boolean correctAnswer;

    public BooleanQuestion() {
        super();
    }

    @Override
    public boolean isCorrect(String answer) {
        try {
            boolean userAnswer = Boolean.parseBoolean(answer);
            return userAnswer == correctAnswer;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String getType() {
        return "boolean";
    }

    // Getters and setters
    public String getLabelTrue() { return labelTrue; }
    public void setLabelTrue(String labelTrue) { this.labelTrue = labelTrue; }

    public String getLabelFalse() { return labelFalse; }
    public void setLabelFalse(String labelFalse) { this.labelFalse = labelFalse; }

    public boolean getCorrectAnswer() { return correctAnswer; }
    public void setCorrectAnswer(boolean correctAnswer) { this.correctAnswer = correctAnswer; }
}