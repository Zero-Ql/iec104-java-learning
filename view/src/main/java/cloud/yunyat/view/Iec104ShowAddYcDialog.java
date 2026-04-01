package cloud.yunyat.view;

import cloud.yunyat.controller.iec104.WindowService;
import cloud.yunyat.controller.iec104.addYcDataController;
import cloud.yunyat.model.pojo.AnalogInput;
import cloud.yunyat.model.service.MessageService;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.function.Consumer;

public class Iec104ShowAddYcDialog implements WindowService {
    @Override
    public void showAddYcDialog(Consumer<AnalogInput> onYcCreated) {
        try {
            FXMLLoader loader = new FXMLLoader(ViewRes.get("/cloud/yunyat/fxml/ycTagProperties.fxml"));
            Parent root = loader.load();

            addYcDataController addYcController = loader.getController();

            addYcController.setYcDataCreated(onYcCreated);

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
