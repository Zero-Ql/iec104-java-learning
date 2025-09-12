package frame;

import enums.IEC104_VariableStructureQualifiers;
import lombok.Data;
import lombok.Setter;

@Data
public class IEC104_MessageInfo {
    /**
     * IOA 3字节 信息对象地址
     * 一般情况下仅前两个字节可用，不同对象地址的最大数量限制为 65535
     * 第三个字节用于构造信息对象地址
     */
    private final int messageAddress;

    /**
     * 值
     */
    @Setter
    private byte[] value;

    /**
     * 质量描述符
     */
    private final byte variableStructureQualifiers;

    public IEC104_MessageInfo(int messageAddress, byte variableStructureQualifiers) {
        this.messageAddress = messageAddress;
        this.variableStructureQualifiers = variableStructureQualifiers;
    }
}
