package raihan.inholland.nl.end_assignment;

public abstract class Question {
    protected String name;
    protected String title;
    protected boolean isRequired;

    public Question() {}

    public Question(String name, String title, boolean isRequired) {
        this.name = name;
        this.title = title;
        this.isRequired = isRequired;
    }

    // Getters and setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public boolean isRequired() { return isRequired; }
    public void setRequired(boolean required) { isRequired = required; }

    // Abstract methods
    public abstract boolean isCorrect(String answer);
    public abstract String getType();
}