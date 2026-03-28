package cloud.yunyat.model.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(builderMethodName = "newDevice")
@AllArgsConstructor
@NoArgsConstructor
public class Rtu {
    private String name;
    private int COA;
    private boolean enable;
}
