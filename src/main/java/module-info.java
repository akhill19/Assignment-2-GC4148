module com.example.week9 {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.week9 to javafx.fxml;
    exports com.example.week9;
}