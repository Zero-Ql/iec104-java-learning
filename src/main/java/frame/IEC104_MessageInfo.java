package frame;

import io.netty.buffer.ByteBuf;
import lombok.Data;

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
    private ByteBuf value;

    /**
     * 召唤限定词(品质描述词)
     */
    private final byte qualityDescriptors;

    public IEC104_MessageInfo(int messageAddress, byte qualityDescriptors) {
        this.messageAddress = messageAddress;
        this.qualityDescriptors = qualityDescriptors;
    }
}
