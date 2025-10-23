package raihan.inholland.nl.end_assignment;

import java.util.ArrayList;
import java.util.List;

public class Quiz {
    private String title = "";
    private String description = "";
    private List<Page> pages = new ArrayList<>();
    private String completedHtml = "";
    private List<String> completedHtmlOnCondition = new ArrayList<>();

    public Quiz() {}

    // Getters and setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title != null ? title : ""; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description != null ? description : ""; }

    public List<Page> getPages() { return pages; }
    public void setPages(List<Page> pages) { this.pages = pages != null ? pages : new ArrayList<>(); }

    public String getCompletedHtml() { return completedHtml; }
    public void setCompletedHtml(String completedHtml) { this.completedHtml = completedHtml != null ? completedHtml : ""; }

    public List<String> getCompletedHtmlOnCondition() { return completedHtmlOnCondition; }
    public void setCompletedHtmlOnCondition(List<String> completedHtmlOnCondition) {
        this.completedHtmlOnCondition = completedHtmlOnCondition != null ? completedHtmlOnCondition : new ArrayList<>();
    }
}