package raihan.inholland.nl.end_assignment;

import java.util.ArrayList;
import java.util.List;

public class Page {
    private int timeLimit = 30;
    private List<Question> elements = new ArrayList<>();

    public Page() {}

    // Getters and setters
    public int getTimeLimit() { return timeLimit; }
    public void setTimeLimit(int timeLimit) { this.timeLimit = timeLimit; }

    public List<Question> getElements() { return elements; }
    public void setElements(List<Question> elements) { this.elements = elements != null ? elements : new ArrayList<>(); }
}