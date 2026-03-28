package cloud.yunyat.controller.iec104;

import cloud.yunyat.model.pojo.Rtu;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import lombok.Setter;

import java.util.function.Consumer;

import static cloud.yunyat.controller.tools.tools.closeStage;

public class addRTUController{
    @Setter
    private Consumer<Rtu> onDeviceCreated;

    @FXML
    private TextField name;
    @FXML
    private TextField coa;

    @FXML
    private void createBtn(ActionEvent event) {

        Rtu rtu = Rtu.newDevice()
                .name(name.getText())
                .COA(Integer.parseInt(coa.getText()))
                .build();

        if (rtu.getName() != null && !rtu.getName().trim().isEmpty()) {
            if (onDeviceCreated != null) {
                onDeviceCreated.accept(rtu);
            }
            closeStage(event);
        }
    }

    @FXML
    private void cancelBtn(ActionEvent event) {
        closeStage(event);
    }
}
