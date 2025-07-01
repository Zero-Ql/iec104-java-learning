package IEC104Frameformat;

import java.util.Arrays;

public class APDUParser {
    public static byte[] parseControlField(byte[] data, int offset) {
        if (data.length < offset + 2) {
            return null; // 数据不足
        }
        return Arrays.copyOfRange(data, offset, offset + 2);
    }

    public static byte parseTypeIdentifier(byte[] data, int offset) {
        if (data.length < offset + 1) {
            return -1; // 数据不足
        }
        return data[offset];
    }

    public static byte parseVariableStructureQualifier(byte[] data, int offset) {
        if (data.length < offset + 1) {
            return -1; // 数据不足
        }
        return data[offset];
    }
}