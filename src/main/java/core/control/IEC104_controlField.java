package core.control;


import lombok.Data;

@Data
public class IEC104_controlField {



    /**
     * 发送序号
     */
    private short Tx;

    /**
     * 接收序号
     */
    private short Rx;

    public IEC104_controlField() {
        this.Tx = 0;
        this.Rx = 0;
    }

    /**
     * 判断 U帧常量
     */
    private static final int uControlFrame = 0x3;

    /**
     * 判断 S帧常量
     */
    private static final int sControlFrame = 0x3;

    /**
     * 判断 I帧常量
     */
    private static final int iControlFrame = 0x0;

    public static boolean isTypeU(byte[] bytes) {

        return (bytes[2] & uControlFrame) == uControlFrame;
    }

    public static boolean isTypeS(byte[] bytes) {
        return (bytes[2] & sControlFrame) == sControlFrame;
    }

    public static boolean isTypeI(byte[] bytes) {
        return (bytes[2] | iControlFrame) == iControlFrame;
    }

    private IEC104_controlField getTxRxData() {
        return this;
    }

    private IEC104_controlField setTxRxData(byte[] bytes) {
        this.Tx = bytes[2];
        this.Rx = bytes[3];
        return this;
    }
}
