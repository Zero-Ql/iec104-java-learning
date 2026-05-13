package cloud.yunyat.model.pojo;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(builderMethodName = "newRtu")
@AllArgsConstructor
@NoArgsConstructor
public class Rtu {
    private String name;
    private int COA;
    private boolean enable;

    @Builder.Default
    private ObservableList<AnalogInput> ycList = FXCollections.observableArrayList();
    @Builder.Default
    private ObservableList<StatusInput> yxList = FXCollections.observableArrayList();
}
