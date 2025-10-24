package frame.apci;

import lombok.Data;

@Data
public class IEC104_ApciMessageDetail {

    /**
     * APDU 长度
     */
    private int apduLen = 0;

    /**
     * 控制域 固定 四字节
     * 发送 两字节
     */
    private short sendOrdinal;

    /**
     * 接收 两字节
     */
    private short recvOrdinal;

    public IEC104_ApciMessageDetail(short sendOrdinal, short recvOrdinal){
        this.sendOrdinal = sendOrdinal;
        this.recvOrdinal = recvOrdinal;
    }
}