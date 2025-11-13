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
//package slave;
//
//import core.codec.IEC104_Decoder;
//import io.netty.channel.ChannelInitializer;
//import io.netty.channel.socket.SocketChannel;
//import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
//import io.netty.handler.codec.LengthFieldPrepender;
//import core.codec.IEC104_Encoder;
//import slave.handler.IEC104_uFrameSlaveHandler;
//
//public class IEC104_ServerInitializer extends ChannelInitializer<SocketChannel> {
//    @Override
//    protected void initChannel(SocketChannel socketChannel) throws Exception {
//        socketChannel.pipeline().addLast(new LengthFieldBasedFrameDecoder(65535, 0, 2, 0, 2));
//        socketChannel.pipeline().addLast(new LengthFieldPrepender(2));
//
//        socketChannel.pipeline().addLast(new IEC104_Decoder());
//        socketChannel.pipeline().addLast(new IEC104_Encoder());
//
//        socketChannel.pipeline().addLast(new IEC104_uFrameSlaveHandler());
//
//        socketChannel.pipeline().addLast(new IEC104_ServerHandler());
//    }
//}
