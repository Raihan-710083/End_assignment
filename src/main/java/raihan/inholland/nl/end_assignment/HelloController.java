package raihan.inholland.nl.end_assignment;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class HelloController {
    @FXML
    private Label welcomeText;

    @FXML
    private Button helloButton;

    @FXML
    private void initialize() {
        welcomeText.setText("Java Quiz Game");
        helloButton.setText("Start Application");
    }

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to Quiz Game!");
        helloButton.setText("JavaFX Works!");
        System.out.println("JavaFX is working correctly!");
    }
}