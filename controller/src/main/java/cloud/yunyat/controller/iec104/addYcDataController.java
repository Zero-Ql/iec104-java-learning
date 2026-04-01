package cloud.yunyat.controller.iec104;

import cloud.yunyat.model.pojo.AnalogInput;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import lombok.Setter;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import static cloud.yunyat.controller.tools.tools.closeStage;

public class addYcDataController implements Initializable {

    @Setter
    private Consumer<AnalogInput> ycDataCreated;

    @FXML
    private ComboBox<String> typeCombo;
    @FXML
    private ComboBox<String> sboCombo;
    @FXML
    private ComboBox<String> qualifierCombo;

    @FXML
    private TextField ioaField;
    @FXML
    private TextField ioaPart1Field;
    @FXML
    private TextField ioaPart2Field;
    @FXML
    private TextField ioaPart3Field;
    @FXML
    private TextField tagNameField;
    @FXML
    private TextField raw1;
    @FXML
    private TextField raw2;
    @FXML
    private TextField scaleTo1;
    @FXML
    private TextField scaleTo2;
    @FXML
    private TextField factor;

    @FXML
    private CheckBox enableStructureIoaCheck;
    @FXML
    private CheckBox enableScaleCheck;

    @FXML
    private ToggleGroup scaleGroup;

    @FXML
    private RadioButton linearRadio;
    @FXML
    private RadioButton scaleFactorRadio;

    @FXML
    private Label Tips;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ioaPart1Field.disableProperty().bind(enableStructureIoaCheck.selectedProperty().not());
        ioaPart2Field.disableProperty().bind(enableStructureIoaCheck.selectedProperty().not());
        ioaPart3Field.disableProperty().bind(enableStructureIoaCheck.selectedProperty().not());
        ioaField.disableProperty().bind(enableStructureIoaCheck.selectedProperty());

        linearRadio.disableProperty().bind(enableScaleCheck.selectedProperty().not());
        scaleFactorRadio.disableProperty().bind(enableScaleCheck.selectedProperty().not());

        enableScaleCheck.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                linearRadio.setSelected(true);
            } else {
                scaleGroup.selectToggle(null);
            }
        });

        var linearInputDisabled = enableScaleCheck.selectedProperty().and(linearRadio.selectedProperty()).not();
        raw1.disableProperty().bind(linearInputDisabled);
        raw2.disableProperty().bind(linearInputDisabled);
        scaleTo1.disableProperty().bind(linearInputDisabled);
        scaleTo2.disableProperty().bind(linearInputDisabled);

        var factorInputDisabled = enableScaleCheck.selectedProperty().and(scaleFactorRadio.selectedProperty()).not();
        factor.disableProperty().bind(factorInputDisabled);

        addTipListener(tagNameField, "当前点名");
        addTipListener(ioaField, "该数据点的点号");
        addTipListener(raw1, "原始起始范围");
        addTipListener(raw2, "原始结束范围");
        addTipListener(scaleTo1, "真实物理起始范围");
        addTipListener(scaleTo2, "真实物理结束范围");
        addTipListener(factor, "直接乘的系数");
    }

    @FXML
    private AnalogInput createYcBtn(ActionEvent event) {

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
