package cloud.yunyat.model.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DeviceWrapper {
    private String displayName;
    private Device device;
}
