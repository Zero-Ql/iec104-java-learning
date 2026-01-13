module cloud.yunyat.app {
    requires cloud.yunyat.view;
    requires  static cloud.yunyat.model;
    requires org.apache.poi.poi;
    requires javafx.graphics;
    requires javafx.fxml;
//    requires javafx.controls;

    opens cloud.yunyat.app to javafx.graphics, javafx.fxml;
}