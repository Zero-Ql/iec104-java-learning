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
     */
    private byte[] iEC104_controlField;

//    public IEC104_ApciMessageDetail(IEC104_controlField iEC104_controlField) {
//        this.iEC104_controlField = iEC104_controlField;
//    }

    public void setTxRxData(byte[] bytes) {
        this.iEC104_controlField[0] = bytes[0];
        this.iEC104_controlField[1] = bytes[1];
        this.iEC104_controlField[2] = bytes[2];
        this.iEC104_controlField[3] = bytes[3];
    }

}