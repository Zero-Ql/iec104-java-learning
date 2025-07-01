package IEC104Frameformat;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ASDUParser {
    // 类型标识符(TypeID)
    private byte typeIdentifier;
    // 可变结构限定词(SQ)
    private byte variableStructureQualified;
    // 公共地址
    private short commonAddress;
    // 信息体地址
    private int ioAddress;
    // 值
    private float value;
}
