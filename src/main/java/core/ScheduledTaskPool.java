package core;

import common.IEC104BasicInstructions;
import io.netty.channel.ChannelHandlerContext;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ScheduledTaskPool {

    /**
     * 通道处理器上下文对象
     */
    private final ChannelHandlerContext ctx;

    /**
     * 保留 2 个线程的线程池对象
     */
    private ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);

    private volatile boolean started = false;

    public ScheduledTaskPool(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }

    /**
     * 发送链路启动帧
     */
    public void sendStartFrame() {
        if (!started) {
            executor.scheduleAtFixedRate(() -> {
                ctx.channel().writeAndFlush(IEC104BasicInstructions.STARTDT_ACT);
            }, 0, 5, TimeUnit.SECONDS);
        }
        started = true;
    }


//    public void sendAGeneralCall() {
//        executor.scheduleAtFixedRate(() -> {
//            ctx.channel().writeAndFlush();
//        });
//    }
}
