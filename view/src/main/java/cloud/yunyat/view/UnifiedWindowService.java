package cloud.yunyat.view;

import cloud.yunyat.controller.iec104.WindowService;
import cloud.yunyat.model.pojo.AnalogInput;
import cloud.yunyat.model.pojo.Device;
import cloud.yunyat.model.pojo.Rtu;

import java.util.function.Consumer;

public class UnifiedWindowService implements WindowService {
    private final WindowService deviceService = new Iec104MasterCommunicationParameters();
    private final WindowService rtuService = new Iec104MasterRtuParameter();
    private final WindowService ycService = new Iec104ShowAddYcDialog();

    @Override
    public void showAddDeviceDialog(Consumer<Device> onDeviceCreated) {
        deviceService.showAddDeviceDialog(onDeviceCreated);
    }

    @Override
    public void showAddRtuDialog(Consumer<Rtu> onRtuCreated) {
        rtuService.showAddRtuDialog(onRtuCreated);
    }

    @Override
    public void showAddYcDialog(Consumer<AnalogInput> onYcCreated) {
        ycService.showAddYcDialog(onYcCreated);
    }

    public static UnifiedWindowService create() {
        return new UnifiedWindowService();
    }
}
