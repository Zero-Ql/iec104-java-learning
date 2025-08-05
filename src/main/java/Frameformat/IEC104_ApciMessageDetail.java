package Frameformat;

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
    private byte[] controlField = null;

}