package core;

import common.IEC104BasicInstructions;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class ScheduledTaskPool {

    private static final Logger log = LogManager.getLogger(ScheduledTaskPool.class);
    /**
     * 通道处理器上下文对象
     */
    private final ChannelHandlerContext ctx;

    /**
     * 保留 2 个线程的线程池对象
     */
    private static final ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);

    private volatile boolean started = false;

    /**
     * 添加任务变量，通过ScheduledExecutorService调度
     */
    private ScheduledFuture<?> startTask;

    public ScheduledTaskPool(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }

    /**
     * 发送链路启动帧
     */
    public void sendStartFrame() {
        // 防止重复发送，如果任务已完成或未提交过则跳过
        if (startTask != null && !startTask.isDone()) return;

        log.info("发送 {} 启动帧", IEC104BasicInstructions.STARTDT_ACT);
        // 在handler中发送启动帧
        ctx.writeAndFlush(IEC104BasicInstructions.STARTDT_ACT);

        // 提交任务并开启延时5秒的计时器
        startTask = executor.schedule(() -> {
            if (ctx.channel().isActive()) {
                log.warn("STARTDT 超时，关闭连接");
                ctx.close();
            }
        }, 5, TimeUnit.SECONDS);
    }

    /**
     * 接收到启动链路帧确认
     */
    public void onReceiveStartDTCon() {
        if (startTask != null) {
            // 取消超时任务
            startTask.cancel(false);
        }
        log.info("收到 STARTDT_CON，链路已激活");
        started = true;
    }

    public void shutdown() {
        // 检测线程池是否关闭
        if (!executor.isShutdown()) {
            // 不再接收新的任务，直到所有任务执行完毕后关闭线程池
            executor.shutdown();
        }
    }

}
