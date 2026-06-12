package cloud.yunyat.controller.tools;

import cloud.yunyat.model.pojo.AnalogInput;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.TreeItem;
import javafx.stage.Stage;

import java.util.stream.Stream;

public class tools {
    public Stream<TreeItem<String>> flatten(TreeItem<String> item) {
        return Stream.concat(
                Stream.of(item),
                item.getChildren().stream().flatMap(this::flatten)
        );
    }



    public static void closeStage(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    public static void showWarning(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }


    // 判断是否名称、点号相同
    public static boolean isExist(AnalogInput oldV, AnalogInput newV) {
        return oldV.getName().equals(newV.getName()) && oldV.getPoint() == newV.getPoint();
    }
}
