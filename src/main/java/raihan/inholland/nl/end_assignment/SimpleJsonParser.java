package raihan.inholland.nl.end_assignment;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class SimpleJsonParser {

    public static Quiz parseQuizFromFile(File file) throws IOException {
        String content = new String(Files.readAllBytes(file.toPath()));
        System.out.println("File content: " + content); // Debug
        return parseQuiz(content);
    }

    private static Quiz parseQuiz(String json) {
        Quiz quiz = new Quiz();

        // Extract title - look for "title":"value"
        String title = extractField(json, "title");
        quiz.setTitle(title);
        System.out.println("Extracted title: " + title); // Debug

        // Extract description
        String description = extractField(json, "description");
        quiz.setDescription(description);
        System.out.println("Extracted description: " + description); // Debug

        // Extract pages
        List<Page> pages = extractPages(json);
        quiz.setPages(pages);
        System.out.println("Extracted pages: " + pages.size()); // Debug

        return quiz;
    }

    private static List<Page> extractPages(String json) {
        List<Page> pages = new ArrayList<>();

        // Find pages array content
        String pagesContent = extractArrayContent(json, "pages");
        if (pagesContent.isEmpty()) return pages;

        // Split into individual page objects
        List<String> pageObjects = splitObjects(pagesContent);

        for (String pageObj : pageObjects) {
            Page page = parsePage(pageObj);
            if (page != null) {
                pages.add(page);
            }
        }

        return pages;
    }

    private static Page parsePage(String pageContent) {
        Page page = new Page();

        // Extract time limit
        String timeLimitStr = extractField(pageContent, "timelimit");
        if (!timeLimitStr.isEmpty()) {
            try {
                page.setTimeLimit(Integer.parseInt(timeLimitStr));
            } catch (NumberFormatException e) {
                page.setTimeLimit(30);
            }
        }

        // Extract elements
        List<Question> elements = extractElements(pageContent);
        page.setElements(elements);

        System.out.println("Page time limit: " + page.getTimeLimit() + ", elements: " + elements.size()); // Debug

        return page;
    }

    private static List<Question> extractElements(String pageContent) {
        List<Question> elements = new ArrayList<>();

        // Find elements array content
        String elementsContent = extractArrayContent(pageContent, "elements");
        if (elementsContent.isEmpty()) return elements;

        // Split into individual question objects
        List<String> questionObjects = splitObjects(elementsContent);

        for (String questionObj : questionObjects) {
            Question question = parseQuestion(questionObj);
            if (question != null) {
                elements.add(question);
            }
        }

        return elements;
    }

    private static Question parseQuestion(String questionContent) {
        String type = extractField(questionContent, "type");
        System.out.println("Question type: " + type); // Debug

        if ("radiogroup".equals(type)) {
            return parseMultipleChoiceQuestion(questionContent);
        } else if ("boolean".equals(type)) {
            return parseBooleanQuestion(questionContent);
        }

        return null;
    }

    private static MultipleChoiceQuestion parseMultipleChoiceQuestion(String questionContent) {
        MultipleChoiceQuestion mcq = new MultipleChoiceQuestion();

        mcq.setName(extractField(questionContent, "name"));
        mcq.setTitle(extractField(questionContent, "title"));
        mcq.setCorrectAnswer(extractField(questionContent, "correctAnswer"));
        mcq.setChoicesOrder(extractField(questionContent, "choicesOrder"));

        // Extract choices
        List<String> choices = extractStringArray(questionContent, "choices");
        mcq.setChoices(choices);

        // Set required to true by default
        mcq.setRequired(true);

        System.out.println("MCQ: " + mcq.getTitle() + ", choices: " + choices.size()); // Debug

        return mcq;
    }

    private static BooleanQuestion parseBooleanQuestion(String questionContent) {
        BooleanQuestion bq = new BooleanQuestion();

        bq.setName(extractField(questionContent, "name"));
        bq.setTitle(extractField(questionContent, "title"));

        String correctAnswerStr = extractField(questionContent, "correctAnswer");
        bq.setCorrectAnswer("true".equalsIgnoreCase(correctAnswerStr));

        bq.setLabelTrue(extractField(questionContent, "labelTrue"));
        bq.setLabelFalse(extractField(questionContent, "labelFalse"));

        // Set defaults
        if (bq.getLabelTrue().isEmpty()) bq.setLabelTrue("True");
        if (bq.getLabelFalse().isEmpty()) bq.setLabelFalse("False");

        // Set required to true by default
        bq.setRequired(true);

        System.out.println("Boolean Q: " + bq.getTitle()); // Debug

        return bq;
    }

    private static String extractField(String json, String fieldName) {
        String pattern = "\"" + fieldName + "\"\\s*:\\s*\"([^\"]*)\"";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = p.matcher(json);
        if (m.find()) {
            return m.group(1);
        }
        return "";
    }

    private static String extractArrayContent(String json, String arrayName) {
        String pattern = "\"" + arrayName + "\"\\s*:\\s*\\[(.*)\\]";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern, java.util.regex.Pattern.DOTALL);
        java.util.regex.Matcher m = p.matcher(json);
        if (m.find()) {
            return m.group(1).trim();
        }
        return "";
    }

    private static List<String> splitObjects(String arrayContent) {
        List<String> objects = new ArrayList<>();
        int braceCount = 0;
        StringBuilder currentObject = new StringBuilder();
        boolean inObject = false;

        for (char c : arrayContent.toCharArray()) {
            if (c == '{') {
                braceCount++;
                inObject = true;
            }
            if (inObject) {
                currentObject.append(c);
            }
            if (c == '}') {
                braceCount--;
                if (braceCount == 0) {
                    objects.add(currentObject.toString());
                    currentObject = new StringBuilder();
                    inObject = false;
                }
            }
        }

        return objects;
    }

    private static List<String> extractStringArray(String json, String arrayName) {
        List<String> result = new ArrayList<>();

        String arrayContent = extractArrayContent(json, arrayName);
        if (arrayContent.isEmpty()) return result;

        // Split by commas but be careful about nested structures
        String[] items = arrayContent.split("\",\\s*\"");
        for (String item : items) {
            // Clean up the string - remove quotes and trim
            String cleanItem = item.replace("\"", "").trim();
            if (!cleanItem.isEmpty()) {
                result.add(cleanItem);
            }
        }

        return result;
    }
}