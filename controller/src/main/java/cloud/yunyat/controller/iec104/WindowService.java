package cloud.yunyat.controller.iec104;

import cloud.yunyat.model.pojo.Device;
import cloud.yunyat.model.pojo.Rtu;

import java.util.function.Consumer;

public interface WindowService {
    void showAddDeviceDialog(Consumer<Device> onDeviceCreated);
    void showAddRtuDialog(Consumer<Rtu> onRtuCreated);
}
