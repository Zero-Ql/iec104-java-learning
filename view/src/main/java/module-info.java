module cloud.yunyat.view {
    requires cloud.yunyat.controller;
    requires javafx.fxml;
    requires javafx.graphics;

    opens cloud.yunyat.view to javafx.base, javafx.fxml;
    exports cloud.yunyat.view;
}