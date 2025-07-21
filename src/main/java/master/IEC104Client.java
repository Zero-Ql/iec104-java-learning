package master;

import core.IEC104Decoder;
import core.IEC104Encoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import master.handler.uFrameMasterHandler;
import slave.handler.uFrameSlaveHandler;

public class IEC104Client {
    private final String host;
    private final int port;

    public IEC104Client(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void run() throws Exception {
        // 创建线程组
        EventLoopGroup group = new NioEventLoopGroup();
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
                            ch.pipeline().addLast("uFrame",new uFrameMasterHandler());
                            // 添加 IEC104 编解码器和业务处理器
                            ch.pipeline().addLast("decoder", new IEC104Decoder());
                            ch.pipeline().addLast("encoder", new IEC104Encoder());
                            ch.pipeline().addLast(new IEC104ClientHandler());
                        }
                    });
            // 启动器使用指定的 host和port 连接服务器，使用 sync 阻塞调用，直到连接成功或失败
            ChannelFuture f = b.connect(host, port).sync();
            // 阻塞等待通道关闭
            f.channel().closeFuture().sync();
        } finally {
            // 关闭线程组
            group.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        String host = "127.0.0.1";
        int port = 2555;
        new IEC104Client(host, port).run();
    }
}