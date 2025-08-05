package Frameformat;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class IEC104_AsduMessageDetail {
    /**
     * 类型标识符 - TypeID
     * 占一字节
     */
    private byte typeIdentifier;
    /**
     * 可变结构限定词 - SQ
     * 占一bit
     */
    private boolean variableStructureQualifiers_sq;

    /**
     * 信息体对象长度 - NumIx
     * 占七bit
     */
    private int messageBodyLength;

    /**
     * 传送原因 - CauseTx
     * 占六bit
     */
    private short transferReason;

    /**
     * 传送原因 - Negative
     * 占一bit
     */
    private boolean transferReason_Negative;

    /**
     * 传送原因 - Test
     * 占一bit
     */
    private boolean transferReason_Test;

    /**
     * 发送方地址 - OA
     * 占一字节
     */
    private short senderAddress;

    /**
     * 公共地址 - Addr
     * 占二字节
     */
    private short publicAddress;

    /**
     * 信息体对象数组
     */
    private IEC104_MessageInfo [] IOA;

}
