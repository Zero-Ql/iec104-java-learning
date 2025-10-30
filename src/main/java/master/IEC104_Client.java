package master;

import core.codec.IEC104_Decoder;
import core.codec.IEC104_Encoder;
import handler.IEC104_uFrameHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import master.handler.IEC104_iFrameMasterHandler;
import master.handler.MasterSeqManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class IEC104_Client {
    private static final Logger log = LogManager.getLogger(IEC104_Client.class);

    private final String host;
    private final int port;

    public IEC104_Client(String host, int port) {
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

                            ch.pipeline().addLast("decoder", new IEC104_Decoder());

                            ch.pipeline().addLast("encoder", new IEC104_Encoder());

                            ch.pipeline().addLast("masterSeqManager", new MasterSeqManager());

                            ch.pipeline().addLast("uFrame", new IEC104_uFrameHandler());

                            ch.pipeline().addLast("iFrame", new IEC104_iFrameMasterHandler());

                            ch.pipeline().addLast("clientHandler", new IEC104_ClientHandler());
                        }
                    });
            log.info("IEC104_Client start...");
            log.info("尝试连接到 {}:{}", host, port);
            // 启动器使用指定的 host和port 连接服务器，使用 sync 阻塞调用，直到连接成功或失败
            ChannelFuture f = b.connect(host, port).sync();
            log.info("连接建立成功");
            // 阻塞等待通道关闭
            f.channel().closeFuture().sync();
        } catch (Exception e) {
            log.error("连接过程中发生异常: ", e);
            throw e;
        } finally {
            log.info("关闭客户端");
            // 关闭线程组
            group.shutdownGracefully();
        }
    }

    /**
     * 运行多个客户端连接到不同的服务器
     *
     * @param servers 服务器地址和端口的映射
     * @throws Exception 连接异常
     */
    public static void runMultipleClients(Map<String, Integer> servers) throws Exception {
        List<IEC104_Client> clients = new ArrayList<>();

        // 创建多个客户端实例
        for (Map.Entry<String, Integer> entry : servers.entrySet()) {
            clients.add(new IEC104_Client(entry.getKey(), entry.getValue()));
        }

        // 并行运行所有客户端
        try (ExecutorService executor = Executors.newFixedThreadPool(clients.size())) {
            try {
                List<Future<?>> futures = new ArrayList<>();

                for (IEC104_Client client : clients) {
                    // 遍历客户端列表并运行
                    Future<?> future = executor.submit(() -> {
                        try {
                            client.run();
                        } catch (Exception e) {
                            log.error("客户端运行异常: ", e);
                        }
                    });
                    futures.add(future);
                }

                // 等待所有客户端完成
                for (Future<?> future : futures) {
                    try {
                        future.get();
                    } catch (Exception e) {
                        log.error("等待客户端完成时发生异常: ", e);
                    }
                }
            } finally {
                executor.shutdown();
            }
        }
    }

    public static void main(String[] args) throws Exception {
        // 单个客户端连接示例
        // String host = "127.0.0.1";
        // int port = 2555;
        // new IEC104_Client(host, port).run();

        // 多个客户端连接示例
        Map<String, Integer> servers = new HashMap<>();
        servers.put("127.0.0.1", 2555);
//        servers.put("127.0.0.1", 2556);
//        servers.put("127.0.0.1", 2557);

        runMultipleClients(servers);
    }
}