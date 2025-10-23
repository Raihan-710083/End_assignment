package raihan.inholland.nl.end_assignment;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class SimpleJsonParser {
    private static final Pattern FIELD_PATTERN = Pattern.compile("\"([^\"]+)\"\\s*:\\s*\"([^\"]*)\"");
    private static final Pattern ARRAY_PATTERN = Pattern.compile("\"([^\"]+)\"\\s*:\\s*\\[(.*)\\]", Pattern.DOTALL);

    private SimpleJsonParser() {
        // Private constructor to prevent instantiation
    }

    public static Quiz parseQuizFromFile(File file) throws IOException {
        validateFile(file);
        String content = readFileContent(file);
        return parseQuiz(content);
    }

    private static void validateFile(File file) throws FileNotFoundException {
        if (file == null) {
            throw new IllegalArgumentException("File cannot be null");
        }
        if (!file.exists()) {
            throw new FileNotFoundException("File does not exist: " + file.getPath());
        }
        if (!file.getName().toLowerCase().endsWith(".json")) {
            throw new IllegalArgumentException("File must be a JSON file");
        }
    }

    private static String readFileContent(File file) throws IOException {
        return new String(Files.readAllBytes(file.toPath()));
    }

    private static Quiz parseQuiz(String json) {
        if (json == null || json.trim().isEmpty()) {
            throw new IllegalArgumentException("JSON content cannot be empty");
        }

        Quiz quiz = new Quiz();
        quiz.setTitle(extractField(json, "title"));
        quiz.setDescription(extractField(json, "description"));
        quiz.setPages(extractPages(json));

        return quiz;
    }

    private static List<Page> extractPages(String json) {
        List<Page> pages = new ArrayList<>();
        String pagesContent = extractArrayContent(json, "pages");

        if (pagesContent.isEmpty()) {
            return pages;
        }

        List<String> pageObjects = splitJsonObjects(pagesContent);
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
        page.setTimeLimit(parseTimeLimit(pageContent));
        page.setElements(extractElements(pageContent));
        return page;
    }

    private static int parseTimeLimit(String pageContent) {
        String timeLimitStr = extractField(pageContent, "timelimit");
        if (!timeLimitStr.isEmpty()) {
            try {
                return Integer.parseInt(timeLimitStr);
            } catch (NumberFormatException e) {
                // Use default time limit
            }
        }
        return 30; // Default time limit
    }

    private static List<Question> extractElements(String pageContent) {
        List<Question> elements = new ArrayList<>();
        String elementsContent = extractArrayContent(pageContent, "elements");

        if (elementsContent.isEmpty()) {
            return elements;
        }

        List<String> questionObjects = splitJsonObjects(elementsContent);
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

        switch (type) {
            case "radiogroup":
                return parseMultipleChoiceQuestion(questionContent);
            case "boolean":
                return parseBooleanQuestion(questionContent);
            default:
                System.err.println("Unsupported question type: " + type);
                return null;
        }
    }

    private static MultipleChoiceQuestion parseMultipleChoiceQuestion(String questionContent) {
        MultipleChoiceQuestion mcq = new MultipleChoiceQuestion();
        mcq.setName(extractField(questionContent, "name"));
        mcq.setTitle(extractField(questionContent, "title"));
        mcq.setCorrectAnswer(extractField(questionContent, "correctAnswer"));
        mcq.setChoicesOrder(extractField(questionContent, "choicesOrder"));
        mcq.setChoices(extractStringArray(questionContent, "choices"));
        mcq.setRequired(true);
        return mcq;
    }

    private static BooleanQuestion parseBooleanQuestion(String questionContent) {
        BooleanQuestion bq = new BooleanQuestion();
        bq.setName(extractField(questionContent, "name"));
        bq.setTitle(extractField(questionContent, "title"));
        bq.setCorrectAnswer(parseBoolean(extractField(questionContent, "correctAnswer")));
        bq.setLabelTrue(getOrDefault(extractField(questionContent, "labelTrue"), "True"));
        bq.setLabelFalse(getOrDefault(extractField(questionContent, "labelFalse"), "False"));
        bq.setRequired(true);
        return bq;
    }

    private static boolean parseBoolean(String value) {
        return "true".equalsIgnoreCase(value);
    }

    private static String getOrDefault(String value, String defaultValue) {
        return value.isEmpty() ? defaultValue : value;
    }

    private static String extractField(String json, String fieldName) {
        Matcher matcher = FIELD_PATTERN.matcher(json);
        while (matcher.find()) {
            if (fieldName.equals(matcher.group(1))) {
                return matcher.group(2);
            }
        }
        return "";
    }

    private static String extractArrayContent(String json, String arrayName) {
        Matcher matcher = ARRAY_PATTERN.matcher(json);
        if (matcher.find() && arrayName.equals(matcher.group(1))) {
            return matcher.group(2).trim();
        }
        return "";
    }

    private static List<String> splitJsonObjects(String arrayContent) {
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

        if (arrayContent.isEmpty()) {
            return result;
        }

        String[] items = arrayContent.split("\",\\s*\"");
        for (String item : items) {
            String cleanItem = item.replace("\"", "").trim();
            if (!cleanItem.isEmpty()) {
                result.add(cleanItem);
            }
        }

        return result;
    }
}