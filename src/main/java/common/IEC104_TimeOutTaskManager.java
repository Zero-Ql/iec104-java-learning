package common;

import config.Piec104Config;
import core.scheduler.IEC104_ScheduledTaskPool;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import util.IEC104Util;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class IEC104_TimeOutTaskManager {
    private final IEC104_ScheduledTaskPool parent;
    private final ChannelHandlerContext ctx;
    private final ScheduledExecutorService executor;
    private final long t1;
    private final AtomicReference<ScheduledFuture<?>> holder = new AtomicReference<>();
    private final AtomicReference<ScheduledFuture<?>> t1Task = new AtomicReference<>();

    private static final Logger log = LogManager.getLogger(IEC104_TimeOutTaskManager.class);

    public IEC104_TimeOutTaskManager(IEC104_ScheduledTaskPool parent, ChannelHandlerContext ctx, ScheduledExecutorService executor, Piec104Config config) {
        this.parent = parent;
        this.ctx = ctx;
        this.executor = executor;
        this.t1 = Long.parseLong(config.getT1());
    }

    /**
     * 启动 T1 任务
     */
    public void startT1Timer() {
        ScheduledFuture<?> newTask = executor.schedule(() -> {
            try {
                if (ctx.channel().isActive()) {
                    log.warn("T1超时，关闭连接");
                    IEC104_ScheduledTaskPool.getFromChannel(ctx).shutdown();
                    ctx.close();
                }
            } catch (Exception e) {
                log.error("执行超时任务异常", e);
            }
        }, t1, TimeUnit.SECONDS);

        if (!t1Task.compareAndSet(null, newTask)) {
            IEC104Util.isCancel(newTask);
            return;
        }

//        log.debug("task run: t1Holder.get()={}, id={}", newTask, newTask == null ? null : System.identityHashCode(newTask));
        holder.set(newTask);
    }

    /**
     * 取消 T1 任务
     */
    public void cancelT1Timer() {
        ScheduledFuture<?> self = holder.get();
        if (self != null && t1Task.compareAndSet(self, null)) {
//            log.debug("task cancel: t1Holder.get()={}, id={}", self, self == null ? null : System.identityHashCode(self));
            IEC104Util.isCancel(self);
        }
    }
}
