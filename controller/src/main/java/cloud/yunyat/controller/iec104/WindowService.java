package cloud.yunyat.controller.iec104;

import cloud.yunyat.model.pojo.AnalogInput;
import cloud.yunyat.model.pojo.Device;
import cloud.yunyat.model.pojo.Rtu;

import java.util.function.Consumer;

public interface WindowService {
    default void showAddDeviceDialog(Consumer<Device> onDeviceCreated){};
    default void showAddRtuDialog(Consumer<Rtu> onRtuCreated){};
    default void showAddYcDialog(Consumer<AnalogInput> onYcCreated){}
}
