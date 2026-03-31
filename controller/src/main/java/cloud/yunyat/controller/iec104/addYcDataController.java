package cloud.yunyat.controller.iec104;

import cloud.yunyat.model.pojo.AnalogInput;
import lombok.Setter;

import java.util.function.Consumer;

public class addYcDataController {
    @Setter
    private Consumer<AnalogInput> ycDataCreated;


}
