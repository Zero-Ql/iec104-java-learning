package cloud.yunyat.model.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RtuWrapper {
    private String displayName;
    private Rtu rtu;
}