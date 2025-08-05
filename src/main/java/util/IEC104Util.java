package util;

import enums.IEC104_UFrameType;

import java.nio.ByteBuffer;

import static enums.IEC104_UFrameType.U_CONTROL_MAP;

public class IEC104Util {
    private static final int controlLength = 4;

    public static IEC104_UFrameType getUControlType(byte[] control) {
        if (control.length < controlLength || control[1] != 0 || control[2] != 0)
            return null;
        int key = ByteBuffer.wrap(control).getInt();
        return U_CONTROL_MAP.get(key);
    }


}
