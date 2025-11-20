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
package impl.iec104.core.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class IEC104_Decoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        // 判断可读数据长度
        if ( byteBuf.readableBytes() < 2)return;
        byteBuf.markReaderIndex();
        // 根据 帧头拆帧
        if (byteBuf.readUnsignedByte() != 0x68){
            // 重置指针
            byteBuf.resetReaderIndex();
            return;
        }

        int apduLength = byteBuf.readUnsignedByte();
        // 当前可读数据长度小于apdu长度，则重置指针
        if (byteBuf.readableBytes() < apduLength) {
            byteBuf.resetReaderIndex();
            return;
        }

        list.add(byteBuf.readBytes(apduLength));
    }
}
