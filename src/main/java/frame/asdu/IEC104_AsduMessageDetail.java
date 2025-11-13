package frame.asdu;

import frame.IEC104_MessageInfo;
import lombok.Data;

import java.util.List;

@Data
public class IEC104_AsduMessageDetail {
    /**
     * 类型标识符 - TypeID
     * 占一字节
     */
    private final byte typeIdentifier;

    /**
     * 可变结构限定词 - SQ
     * 占一bit
     * 信息体对象长度 - NumIx
     * 占七bit
     */
    private final byte variableStructureQualifiers;

    /**
     * 占一bit
     * 传送原因 - Test
     * 占一bit
     * 传送原因 - CauseTx
     * 占六bit
     * 传送原因 - Negative
     */
    private final byte transferReason;

    /**
     * 发送方地址 - OA
     * 占一字节
     */
    private final byte senderAddress;

    /**
     * 公共地址 - Addr
     * 占二字节
     */
    private final short publicAddress;

    /**
     * 信息体对象数组
     */
    private final List<IEC104_MessageInfo> IOA;

    private IEC104_AsduMessageDetail(Builder builder) {
        this.typeIdentifier = builder.typeIdentifier;
        this.variableStructureQualifiers = builder.variableStructureQualifiers;
        this.transferReason = builder.transferReason;
        this.senderAddress = builder.senderAddress;
        this.publicAddress = builder.publicAddress;
        this.IOA = builder.IOA;
    }

    public static class Builder {
        private final byte typeIdentifier;

        private final byte variableStructureQualifiers;

        private final byte transferReason;

        private final byte senderAddress;

        private final short publicAddress;

        private final List<IEC104_MessageInfo> IOA;

        public Builder(byte typeIdentifier, byte variableStructureQualifiers, byte transferReason, byte senderAddress, short publicAddress, List<IEC104_MessageInfo> IOA) {
            this.typeIdentifier = typeIdentifier;
            this.variableStructureQualifiers = variableStructureQualifiers;
            this.transferReason = transferReason;
            this.senderAddress = senderAddress;
            this.publicAddress = publicAddress;
            this.IOA = IOA;
        }

        public IEC104_AsduMessageDetail build() {
            return new IEC104_AsduMessageDetail(this);
        }
    }
}
