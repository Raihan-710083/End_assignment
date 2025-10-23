package raihan.inholland.nl.end_assignment;

import java.util.List;

public class MultipleChoiceQuestion extends Question {
    private List<String> choices;
    private String correctAnswer;
    private String choicesOrder;

    public MultipleChoiceQuestion() {
        super();
    }

    @Override
    public boolean isCorrect(String answer) {
        return correctAnswer != null && correctAnswer.equals(answer);
    }

    @Override
    public String getType() {
        return "radiogroup";
    }

    // Getters and setters
    public List<String> getChoices() { return choices; }
    public void setChoices(List<String> choices) { this.choices = choices; }

    public String getCorrectAnswer() { return correctAnswer; }
    public void setCorrectAnswer(String correctAnswer) { this.correctAnswer = correctAnswer; }

    public String getChoicesOrder() { return choicesOrder; }
    public void setChoicesOrder(String choicesOrder) { this.choicesOrder = choicesOrder; }
}