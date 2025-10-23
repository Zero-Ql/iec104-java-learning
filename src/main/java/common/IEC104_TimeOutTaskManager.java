package common;

import config.Piec104Config;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import util.IEC104Util;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class IEC104_TimeOutTaskManager {
    private final ChannelHandlerContext ctx;
    private final ScheduledExecutorService executor;
    private final Piec104Config config;
    private final AtomicReference<ScheduledFuture<?>> t1Task = new AtomicReference<>();

    private static final Logger log = LogManager.getLogger(IEC104_TimeOutTaskManager.class);

    public IEC104_TimeOutTaskManager(ChannelHandlerContext ctx, ScheduledExecutorService executor, Piec104Config config) {
        this.ctx = ctx;
        this.executor = executor;
        this.config = config;
    }

    /**
     * 启动 T1 任务
     */
    public void startT1Timer() {
        ScheduledFuture<?> task = t1Task.get();
        IEC104Util.isCancel(task);
        ScheduledFuture<?> newTask = executor.schedule(() -> {
            try {
                log.warn("T1超时，关闭连接");
                ctx.close();
            } catch (Exception e) {
                log.error("执行超时任务异常", e);
            }
        }, Long.parseLong(config.getT1()), TimeUnit.SECONDS);
        // 使用 CAS乐观锁非阻塞更新
        t1Task.compareAndSet(task, newTask);
    }

    /**
     * 取消 T1 任务
     */
    public void cancelT1Timer() {
        IEC104Util.isCancel(t1Task.get());
    }
}
