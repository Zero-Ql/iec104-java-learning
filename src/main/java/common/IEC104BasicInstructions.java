package common;

public class IEC104BasicInstructions {
    /**
     * 定义启动数据传输的请求帧
     */
    public static final byte[] STARTDT_ACT = new byte[]{0x68, 0x04, 0x07, 0x00, 0x00, 0x00};

    /**
     * 定义启动数据传输的确认帧
     */
    public static final byte[] STARTDT_CON = new byte[]{0x68, 0x04, 0xB, 0x00, 0x00, 0x00};

    /**
     * 定义停止数据传输的请求帧
     */
    public static final byte[] STOPDT_ACT = new byte[]{0x68, 0x04, 0x13, 0x00, 0x00, 0x00};

    /**
     * 定义停止数据传输的确认帧
     */
    public static final byte[] STOPDT_CON = new byte[]{0x68, 0x04, 0x23, 0x00, 0x00, 0x00};

    /**
     * 定义测试帧的请求帧
     */
    public static final byte[] TESTFR_ACT = new byte[]{0x68, 0x04, 0x43, 0x00, 0x00, 0x00};

    /**
     * 定义测试帧的确认帧
     */
    public static final byte[] TESTFR_CON = new byte[]{0x68, 0x04, (byte) 0x83, 0x00, 0x00, 0x00};

}
