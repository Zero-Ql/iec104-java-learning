package util;

import java.nio.ByteBuffer;

public class ByteUtil {

    public static byte[] subBytes(byte[] source, int startIndex, int offset) {
        byte[] result = new byte[offset];
        System.arraycopy(source, startIndex, result, 0, offset);
        return result;
    }

    public static int byteToInt(byte[] bytes) {
        int value = 0;
        for (int i = 0; i < 4; i++) {
            int shift = (3 - i) * 8;
            value += (bytes[i] & 0xFF) << shift;
        }
        return value;
    }

    public static byte[] intToByte(int value) {
        byte[] bytes = new byte[4];
        bytes[0] = (byte) ((value >> 24) & 0xFF);
        bytes[1] = (byte) ((value >> 16) & 0xFF);
        bytes[2] = (byte) ((value >> 8) & 0xFF);
        bytes[3] = (byte) (value & 0xFF);
        return bytes;
    }

    public static short byteToShort(byte[] bytes) {
        short value = 0;
        for (int i = 0; i < 2; i++) {
            short shift = (short) ((1 - i) * 8);
            value += (bytes[i] & 0xFF) << shift;
        }
        return value;
    }

    public static byte[] shortToByte(short value) {
        byte[] bytes = new byte[2];
        bytes[0] = (byte) ((value >> 8) & 0xFF);
        bytes[1] = (byte) (value & 0xFF);
        return bytes;
    }

    public static byte[] customStructureToBytes(boolean sq, short numIx, boolean negative, boolean test, short causeTx, byte senderAddress) {
        numIx = (short) (sq ? numIx | (1 << 7) : numIx & ~(1 << 7));
        causeTx = (short) (negative ? causeTx | (1 << 7) : causeTx & ~(1 << 7));
        causeTx = (short) (test ? causeTx | (1 << 6) : causeTx & ~(1 << 6));

        byte[] result = new byte[3];
        result[0] = (byte) numIx;
        result[1] = (byte) causeTx;
        result[2] = senderAddress;
        return result;
    }
}
