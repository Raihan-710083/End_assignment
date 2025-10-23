module raihan.inholland.nl.end_assignment {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.json;


    opens raihan.inholland.nl.end_assignment to javafx.fxml;
    exports raihan.inholland.nl.end_assignment;
}