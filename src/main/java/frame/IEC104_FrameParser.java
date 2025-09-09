package frame;


import core.control.IEC104_controlField;
import frame.apci.IEC104_ApciMessageDetail;
import frame.asdu.IEC104_AsduMessageDetail;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 拼接完整帧
 */
@Data
@AllArgsConstructor
public class IEC104_FrameParser {
    private IEC104_ApciMessageDetail apciMessageDetail;
    private IEC104_controlField iEC104_controlField;
    private IEC104_AsduMessageDetail asduMessageDetail;
    private IEC104_MessageInfo iEC104_messageInfo;
}