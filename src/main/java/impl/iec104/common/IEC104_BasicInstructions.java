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
package impl.iec104.common;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class IEC104_BasicInstructions {
    /**
     * 定义启动数据传输的请求帧
     */
    public static final ByteBuf STARTDT_ACT = Unpooled.unmodifiableBuffer(Unpooled.wrappedBuffer(new byte[]{0x07, 0x00, 0x00, 0x00}));

    /**
     * 定义启动数据传输的确认帧
     */
    public static final ByteBuf STARTDT_CON = Unpooled.unmodifiableBuffer(Unpooled.wrappedBuffer(new byte[]{0xB, 0x00, 0x00, 0x00}));

    /**
     * 定义停止数据传输的请求帧
     */
    public static final ByteBuf STOPDT_ACT = Unpooled.unmodifiableBuffer(Unpooled.wrappedBuffer(new byte[]{0x13, 0x00, 0x00, 0x00}));

    /**
     * 定义停止数据传输的确认帧
     */
    public static final ByteBuf STOPDT_CON = Unpooled.unmodifiableBuffer(Unpooled.wrappedBuffer(new byte[]{0x23, 0x00, 0x00, 0x00}));

    /**
     * 定义测试帧的请求帧
     */
    public static final ByteBuf TESTFR_ACT = Unpooled.unmodifiableBuffer(Unpooled.wrappedBuffer(new byte[]{0x43, 0x00, 0x00, 0x00}));

    /**
     * 定义测试帧的确认帧
     */
    public static final ByteBuf TESTFR_CON = Unpooled.unmodifiableBuffer(Unpooled.wrappedBuffer(new byte[]{(byte) 0x83, 0x00, 0x00, 0x00}));

    private IEC104_BasicInstructions() {
    }
}
