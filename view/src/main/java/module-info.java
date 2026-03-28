module cloud.yunyat.view {
    requires cloud.yunyat.controller;
    requires javafx.fxml;
    requires javafx.graphics;
    requires cloud.yunyat.model;

    opens cloud.yunyat.view to javafx.base, javafx.fxml;
    exports cloud.yunyat.view;
}