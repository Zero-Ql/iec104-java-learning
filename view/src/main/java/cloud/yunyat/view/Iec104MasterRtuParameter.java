package cloud.yunyat.view;

import cloud.yunyat.controller.iec104.WindowService;
import cloud.yunyat.controller.iec104.addRTUController;
import cloud.yunyat.model.pojo.Device;
import cloud.yunyat.model.pojo.Rtu;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.function.Consumer;

public class Iec104MasterRtuParameter implements WindowService {
    @Override
    public void showAddRtuDialog(Consumer<Rtu> onRtuCreated) {
        try {
            FXMLLoader loader = new FXMLLoader(ViewRes.get("/cloud/yunyat/fxml/iec104MasterRtuParameter.fxml"));
            Parent root = loader.load();

            addRTUController addRtuController = loader.getController();
            addRtuController.setOnDeviceCreated(onRtuCreated);

            // 2. 创建新舞台 (Stage)
            Stage stage = new Stage();
            stage.setTitle("参数设置");

            // 3. 设置模态（点击此窗口时，主窗口不可操作）
            stage.initModality(Modality.APPLICATION_MODAL);

            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
