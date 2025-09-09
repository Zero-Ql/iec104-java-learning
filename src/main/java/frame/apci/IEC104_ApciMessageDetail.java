package frame.apci;

import core.control.IEC104_controlField;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class IEC104_ApciMessageDetail {

    /**
     * 起始字节 固定 一字节
     */
    private byte start = 0x68;

    /**
     * APDU 长度
     */
    private int apduLen = 0;
    /**
     * 控制域 固定 四字节
     */
    private IEC104_controlField iEC104_controlField;

}