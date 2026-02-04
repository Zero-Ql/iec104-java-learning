package cloud.yunyat.controller.iec104;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lombok.Setter;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.function.Consumer;

public class addDeviceController implements Initializable {
    @Setter
    private Consumer<Map<String, String>> onDeviceCreated;

    @FXML
    private TextField gmtField;
    @FXML
    private TextField deviceNameField;
    @FXML
    private TextField ipNameField;
    @FXML
    private TextField portNameField;
    @FXML
    private TextField t1NameField;
    @FXML
    private TextField t2NameField;
    @FXML
    private TextField t3NameField;
    @FXML
    private TextField wNameField;
    @FXML
    private TextField generalInterrogationNameField;
    @FXML
    private TextField clockSynchronizationNameField;

    @FXML
    private CheckBox enableTimezoneCheck;

    @FXML
    private Label Tips;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        gmtField.disableProperty().bind(enableTimezoneCheck.selectedProperty().not());

        addTipListener(gmtField, "目标时区");
        addTipListener(deviceNameField, "主站名称");
        addTipListener(ipNameField, "对端ip地址");
        addTipListener(portNameField, "对端端口号");
        addTipListener(t1NameField, "在发送方发送一个 I 帧或 U 帧后,等待超时时间");
        addTipListener(t2NameField, "在接收方收到 I 帧后,等待超时时间");
        addTipListener(t3NameField, "发送 U 测试帧时间");
        addTipListener(wNameField, "接收方在累计接收 W 个 I 帧后发出 S 帧时间");
        addTipListener(generalInterrogationNameField, "总召发送间隔");
        addTipListener(clockSynchronizationNameField, "发送时钟同步时间");
    }

    @FXML
    private void createBtn(ActionEvent event) {
        Map<String, String> deviceData = new HashMap<>();
        deviceData.put("name", deviceNameField.getText());
        deviceData.put("ip", ipNameField.getText());
        deviceData.put("port", portNameField.getText());
        deviceData.put("T1", t1NameField.getText());
        deviceData.put("T2", t2NameField.getText());
        deviceData.put("T3", t3NameField.getText());
        deviceData.put("W", wNameField.getText());
        deviceData.put("G", generalInterrogationNameField.getText());
        deviceData.put("C", clockSynchronizationNameField.getText());
        deviceData.put("gmt", gmtField.getText());
        if (deviceData.get("name") != null && !deviceData.get("name").trim().isEmpty()) {
            if (onDeviceCreated != null) {
                onDeviceCreated.accept(deviceData);
            }
            closeStage(event);
        }
    }

    @FXML
    private void cancelBtn(ActionEvent event) {
        closeStage(event);
    }

    private void closeStage(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    private void addTipListener(Control control, String tipText) {
        control.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                Tips.setText("提示：" + tipText);
            } else {
                Tips.setText("就绪");
            }
        });
    }
}
