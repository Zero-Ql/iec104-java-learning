module cloud.yunyat.controller {
    requires cloud.yunyat.model;
    requires javafx.fxml;
    requires javafx.controls;
    requires static lombok;
    requires org.apache.logging.log4j.core;
    requires org.apache.logging.log4j;
    requires io.netty.buffer;
    requires jdk.compiler;

    exports cloud.yunyat.controller;
    exports cloud.yunyat.controller.iec104;
    opens cloud.yunyat.controller to javafx.graphics, javafx.fxml;
    opens cloud.yunyat.controller.iec104 to javafx.graphics, javafx.fxml;
}