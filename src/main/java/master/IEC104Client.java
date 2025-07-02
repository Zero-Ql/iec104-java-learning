package master;

import core.IEC104Decoder;
import core.IEC104Encoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import slave.handler.uFrameSlaveHandler;

public class IEC104Client {
    private final String host;
    private final int port;

    public IEC104Client(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void run() throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ch.pipeline().addLast("uFrame",new uFrameSlaveHandler());
                            // 添加 IEC104 编解码器和业务处理器
                            ch.pipeline().addLast("decoder", new IEC104Decoder());
                            ch.pipeline().addLast("encoder", new IEC104Encoder());
                            ch.pipeline().addLast(new IEC104ClientHandler());
                        }
                    });
            // 开启一个 Netty 客户端
            ChannelFuture f = b.connect(host, port).sync();
            f.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        String host = "127.0.0.1";
        int port = 2555;
        new IEC104Client(host, port).run();
    }
}