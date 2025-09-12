package common;

import config.piec104Config;
import core.scheduler.IEC104_ScheduledTaskPool;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import util.IEC104Util;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class IEC104_UFrameTaskManager {
    private final IEC104_ScheduledTaskPool parent;
    private final ChannelHandlerContext ctx;
    private final ScheduledExecutorService executor;
    private final piec104Config config;
    private final AtomicReference<ScheduledFuture<?>> startTask = new AtomicReference<>();
    private final AtomicReference<ScheduledFuture<?>> t3Task = new AtomicReference<>();
    private static final Logger log = LogManager.getLogger(IEC104_UFrameTaskManager.class);

    public IEC104_UFrameTaskManager(IEC104_ScheduledTaskPool parent, ChannelHandlerContext ctx, ScheduledExecutorService executor, piec104Config config) {
        this.parent = parent;
        this.ctx = ctx;
        this.executor = executor;
        this.config = config;
    }

    /**
     * 发送链路启动帧
     * <p>
     * 如果当前没有正在进行的启动任务，则发送STARTDT_ACT帧并启动超时检测任务
     * 超时时间为15秒，超时后将自动关闭连接
     */
    public void sendStartFrame() {

        // 通过原子引用获取当前任务
        ScheduledFuture<?> currentTask = startTask.get();

        // 防止重复发送，如果任务已完成或未提交过则跳过
        if (currentTask != null && !currentTask.isDone()) {
            return;
        }

        log.info("发送 {} 启动帧", IEC104_BasicInstructions.STARTDT_ACT);
        // 在handler中发送启动帧
        ctx.writeAndFlush(IEC104_BasicInstructions.STARTDT_ACT.retain());

        // 提交任务并开启 T1 计时器
        ScheduledFuture<?> newTask = executor.schedule(() -> {
            try {
                if (ctx.channel().isActive()) {
                    log.warn("STARTDT 超时，关闭连接");
                    ctx.close();
                }
            } catch (Exception e) {
                log.error("执行超时任务异常", e);
            }
        }, Long.parseLong(config.getT1()), TimeUnit.SECONDS);

        // 使用 CAS乐观锁非阻塞更新
        startTask.compareAndSet(currentTask, newTask);
    }

    public void onReceiveStartDTCon() {
        IEC104Util.isCancel(startTask.get());
    }

    public void sendTestFrame() {
        // 获取t3Task的原子引用
        ScheduledFuture<?> currentTask = t3Task.get();

        // 检查是否有任务未完成，有则取消任务(重置T3)
        IEC104Util.isCancel(currentTask);

        ScheduledFuture<?> newTask = executor.schedule(() -> {
            log.info("发送 {} 测试帧", IEC104_BasicInstructions.TESTFR_ACT);
            // 经过 T3 时间没有报文交互，发送 TESTFR_ACT U测试帧
            ctx.writeAndFlush(IEC104_BasicInstructions.TESTFR_ACT.retain());
            // 开启 T1 计时器
            parent.startT1Timer();
        }, Long.parseLong(config.getT3()), TimeUnit.SECONDS);
        t3Task.compareAndSet(currentTask, newTask);
    }
}
