package cloud.yunyat.controller.iec104;

import cloud.yunyat.model.pojo.Device;
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
import java.util.ResourceBundle;
import java.util.function.Consumer;

import static cloud.yunyat.controller.tools.tools.closeStage;

public class addDeviceController implements Initializable {
    @Setter
    private Consumer<Device> onDeviceCreated;

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
        // 绑定GMT时区字段的禁用状态到时区启用复选框的反向选择状态
        // 当enableTimezoneCheck未被选中时，gmtField将被禁用；当被选中时，gmtField可用
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

        Device device = Device.newDevice()
                .name(deviceNameField.getText())
                .ip(ipNameField.getText())
                .port(Integer.parseInt(portNameField.getText()))
                .t1(Integer.parseInt(t1NameField.getText()))
                .t2(Integer.parseInt(t2NameField.getText()))
                .t3(Integer.parseInt(t3NameField.getText()))
                .w(Integer.parseInt(wNameField.getText()))
                .generalInterrogation(Integer.parseInt(generalInterrogationNameField.getText()))
                .clockSynchronization(Integer.parseInt(clockSynchronizationNameField.getText()))
                .gmt(gmtField.getText())
                .enableTimezone(enableTimezoneCheck.isSelected())
                .build();

        if (device.getName() != null && !device.getName().trim().isEmpty()) {
            if (onDeviceCreated != null) {
                onDeviceCreated.accept(device);
            }
            closeStage(event);
        }
    }

    @FXML
    private void cancelBtn(ActionEvent event) {
        closeStage(event);
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
