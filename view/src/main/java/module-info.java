module cloud.yunyat.view {
    requires cloud.yunyat.controller;
    requires javafx.fxml;
    requires cloud.yunyat.model;
    requires javafx.controls;

    opens cloud.yunyat.view to javafx.base, javafx.fxml;
    exports cloud.yunyat.view;
}