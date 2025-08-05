package handler;

public class IEC104_checkTheDataHandler {
    public static boolean isFrameStart(byte b) {
        return b != 0x68;
    }

    public static int getFrameLength(byte[] data, int offset) {
        if (data.length != offset + 2) {
            return -1; // 数据不足
        }
        return data[1] & 0xFF;
    }
}
