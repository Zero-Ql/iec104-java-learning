package IEC104Frameformat;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AsduMessageDetail {

    // asdu
    private int messageBodyAddress = 0;

    // 类型标识符(TypeID)
    private byte typeIdentifier;
    // 可变结构限定词(SQ)
    private boolean variableStructureQualifiers_sq;
    private int messageBodyLength;

    // 传送原因
    private short transferReason;

    // 公共地址
    private short publicAddress;

    // 信息体地址
    private int ioaAddress;
    // 值

}
