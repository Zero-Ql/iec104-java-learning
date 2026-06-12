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
package cloud.yunyat.model.master;

import cloud.yunyat.model.impl.iec104.core.codec.IEC104_Decoder;
import cloud.yunyat.model.impl.iec104.core.codec.IEC104_Encoder;
import cloud.yunyat.model.impl.iec104.handler.IEC104_uFrameHandler;
import cloud.yunyat.model.service.MessageManager;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import cloud.yunyat.model.master.handler.IEC104_iFrameMasterHandler;
import cloud.yunyat.model.master.handler.MasterSeqManager;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class IEC104_Client {
    private static final Logger log = LogManager.getLogger(IEC104_Client.class);

    @Getter
    private final MessageManager messageManager = new MessageManager();

    private final String host;
    private final int port;
    private final List<Short> rtuCoasList;

    private EventLoopGroup group;
    private Channel channel;

    public IEC104_Client(String host, int port, List<Short> rtuCoasList) {
        this.host = host;
        this.port = port;
        this.rtuCoasList = rtuCoasList;
    }

    public void run() throws Exception {
        // 创建线程组
        group = new NioEventLoopGroup();
        try {
            // 创建客户端启动器
            Bootstrap b = new Bootstrap();
            // 绑定线程组到启动器
            b.group(group)
                    // 指定使用 NioSocketChannel 通道
                    .channel(NioSocketChannel.class)
                    // 启用 TCP 心跳机制，定期发送心跳
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    // 添加通道处理器流水线；在连接建立后，初始化 SocketChannel 处理器链
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {

                            ch.pipeline().addLast("decoder", new IEC104_Decoder());

                            ch.pipeline().addLast("encoder", new IEC104_Encoder());

                            ch.pipeline().addLast("masterSeqManager", new MasterSeqManager());

                            ch.pipeline().addLast("uFrame", new IEC104_uFrameHandler(rtuCoasList));

                            ch.pipeline().addLast("iFrame", new IEC104_iFrameMasterHandler(messageManager));

                            ch.pipeline().addLast("clientHandler", new IEC104_ClientHandler());
                        }
                    });
            log.info("IEC104_Client start...");
            log.info("尝试连接到 {}:{}", host, port);
            // 启动器使用指定的 host和port 连接服务器，使用 sync 阻塞调用，直到连接成功或失败
            ChannelFuture f = b.connect(host, port).sync();

            this.channel = f.channel();

            log.info("连接建立成功");
            // 阻塞等待通道关闭
            this.channel.closeFuture().sync();
        } catch (Exception e) {
            log.error("连接过程中发生异常: ", e);
            throw e;
        } finally {
            log.info("关闭客户端");
            if (group != null && !group.isShutdown()) {
                group.shutdownGracefully();
            }
        }
    }

    /**
     * 新增断开连接方法
     */
    public void stop() {
        log.info("正在关闭客户端连接...");
        if (channel != null && channel.isActive()) {
            channel.close();
        }
        if (group != null && !group.isShutdown()) {
            group.shutdownGracefully();
        }
    }
}