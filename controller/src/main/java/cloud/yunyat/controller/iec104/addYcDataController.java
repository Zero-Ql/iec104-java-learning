package cloud.yunyat.controller.iec104;

import cloud.yunyat.model.impl.iec104.enums.IEC104_TypeIdentifier;
import cloud.yunyat.model.pojo.AnalogInput;
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
    private ComboBox<IEC104_TypeIdentifier> typeCombo;
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

        typeCombo.getItems().setAll(IEC104_TypeIdentifier.values());
        typeCombo.setValue(IEC104_TypeIdentifier.M_SP_NA_1);

        addTipListener(tagNameField, "当前点名");
        addTipListener(ioaField, "该数据点的点号");
        addTipListener(raw1, "原始起始范围");
        addTipListener(raw2, "原始结束范围");
        addTipListener(scaleTo1, "真实物理起始范围");
        addTipListener(scaleTo2, "真实物理结束范围");
        addTipListener(factor, "直接乘的系数");
    }

    @FXML
    private void createYcBtn(ActionEvent event) {
        try {
            String name = tagNameField.getText().trim();

            if (name.isEmpty()) {
                Tips.setText("错误：点位名称不能为空！");
                return;
            }

            int point = getPoint();

            AnalogInput newYc = getAnalogInput(name, point);

            if (ycDataCreated != null) {
                ycDataCreated.accept(newYc);
            }

            closeStage(event);

        } catch (NumberFormatException e) {
            Tips.setText("提示：请输入正确的数字格式！");
        } catch (Exception e) {
            Tips.setText("提示：发生未知错误！");
        }
    }

    private int getPoint() {
        int point;
        if (enableStructureIoaCheck.isSelected()) {
            // 结构化 IOA 通常由 3 个字节组成。
            // 工业标准算法：(Part3) + (Part2 * 256) + (Part1 * 65536)
            int p1 = Integer.parseInt(ioaPart1Field.getText().trim());
            int p2 = Integer.parseInt(ioaPart2Field.getText().trim());
            int p3 = Integer.parseInt(ioaPart3Field.getText().trim());
            point = (p1 << 16) | (p2 << 8) | p3;
        } else {
            // 普通 IOA 模式
            point = Integer.parseInt(ioaField.getText().trim());
        }
        return point;
    }

    private AnalogInput getAnalogInput(String name, int point) {
        IEC104_TypeIdentifier typeId = typeCombo.getValue();

        double coefficient = 1.0;
        if (enableScaleCheck.isSelected()) {
            if (scaleFactorRadio.isSelected()) {
                // 如果选了固定系数
                coefficient = Double.parseDouble(factor.getText().trim());
            } else if (linearRadio.isSelected()) {
                // 如果选了线性转换，需要算出系数 (ScaleTo 范围 / Raw 范围)
                double r1 = Double.parseDouble(raw1.getText().trim());
                double r2 = Double.parseDouble(raw2.getText().trim());
                double s1 = Double.parseDouble(scaleTo1.getText().trim());
                double s2 = Double.parseDouble(scaleTo2.getText().trim());
                if (r2 != r1) {
                    coefficient = (s2 - s1) / (r2 - r1);
                }
            }
        }

        return new AnalogInput(
                name,
                typeId,
                point,
                0.0,
                0,
                0,
                0,
                coefficient
        );
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
