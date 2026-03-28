package cloud.yunyat.view;

import cloud.yunyat.controller.iec104.WindowService;
import cloud.yunyat.model.pojo.Device;
import cloud.yunyat.model.pojo.Rtu;

import java.util.function.Consumer;

public class UnifiedWindowService implements WindowService {
    private final WindowService deviceService = new Iec104MasterCommunicationParameters();
    private final WindowService rtuService = new Iec104MasterRtuParameter();

    @Override
    public void showAddDeviceDialog(Consumer<Device> onDeviceCreated) {
        deviceService.showAddDeviceDialog(onDeviceCreated);
    }

    @Override
    public void showAddRtuDialog(Consumer<Rtu> onRtuCreated) {
        rtuService.showAddRtuDialog(onRtuCreated);
    }
}
