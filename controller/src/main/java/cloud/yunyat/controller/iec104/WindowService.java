package cloud.yunyat.controller.iec104;

import java.util.Map;
import java.util.function.Consumer;

public interface WindowService {
    void showAddDeviceDialog(Consumer<Map<String, String>> onDeviceCreated);
}
