/*
 * IEC 60870-5-104 Protocol Implementation
 * Copyright (C) 2025 QSky
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */
package impl.iec104.util;

public class ByteUtil {

    private static final int SIGN_BIT_MASK = 1 << 7;
    private static final int TEST_BIT_MASK = 1 << 6;

    /**
     * 判断 U帧常量
     */
    private static final int uControlFrame = 0x03;

    /**
     * 判断 S帧常量
     */
    private static final int sControlFrame = 0x03;

    /**
     * 判断 I帧常量
     */
    private static final int iControlFrame = 0x01;

    public static boolean isTypeU(byte bytes) {
        return (bytes & uControlFrame) == 3;
    }

    public static boolean isTypeS(byte bytes) {
        return (bytes & sControlFrame) == 1;
    }

    public static boolean isTypeI(byte bytes) {
        return (bytes & iControlFrame) == 0;
    }

    /**
     * 从指定的字节数组中截取子数组
     *
     * @param source 原始字节数组
     * @param startIndex 截取开始位置的索引
     * @param offset 要截取的字节长度
     * @return 截取后的新字节数组
     */
    public static byte[] subBytes(byte[] source, int startIndex, int offset) {
        // 创建结果数组并复制指定范围的字节数据
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

    // 将 short 的字节序反转（大端 ↔ 小端）
    public static short swapShort(short value) {
        int b1 = (value >> 8) & 0xFF;
        int b2 = (value >> 0) & 0xFF;
        return (short) ((b2 << 8) | b1);
    }

    public static byte[] shortToByte(short value) {
        byte[] bytes = new byte[2];
        bytes[0] = (byte) ((value >> 8) & 0xFF);
        bytes[1] = (byte) (value & 0xFF);
        return bytes;
    }

    private static short setBit(short vessel, int bitMask, boolean value) {
        return (short) (value ? vessel | bitMask : vessel & ~bitMask);
    }

    public static byte[] customStructureToBytes(boolean sq, short numIx, boolean negative, boolean test, short causeTx, byte senderAddress) {
        numIx = setBit(numIx, SIGN_BIT_MASK, sq);
        causeTx = setBit(causeTx, SIGN_BIT_MASK, negative);
        causeTx = setBit(causeTx, TEST_BIT_MASK, test);

        byte[] result = new byte[3];
        result[0] = (byte) numIx;
        result[1] = (byte) causeTx;
        result[2] = senderAddress;
        return result;
    }
}
