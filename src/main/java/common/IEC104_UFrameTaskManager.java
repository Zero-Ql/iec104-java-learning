package common;

import config.Piec104Config;
import core.scheduler.IEC104_ScheduledTaskPool;
import frame.IEC104_FrameBuilder;
import frame.apci.IEC104_ApciMessageDetail;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.CompositeByteBuf;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import util.IEC104Util;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class IEC104_UFrameTaskManager {
    private final long t1;
    private final long t3;
    private final IEC104_ScheduledTaskPool parent;
    private final ChannelHandlerContext ctx;
    private final ScheduledExecutorService executor;
    private final AtomicReference<ScheduledFuture<?>> startTask = new AtomicReference<>();
    private final AtomicReference<ScheduledFuture<?>> startHolder = new AtomicReference<>();
    private final AtomicReference<ScheduledFuture<?>> testHolder = new AtomicReference<>();
    private final AtomicReference<ScheduledFuture<?>> t3Task = new AtomicReference<>();
    private static final Logger log = LogManager.getLogger(IEC104_UFrameTaskManager.class);

    public IEC104_UFrameTaskManager(IEC104_ScheduledTaskPool parent, ChannelHandlerContext ctx, ScheduledExecutorService executor, Piec104Config config) {
        this.parent = parent;
        this.ctx = ctx;
        this.executor = executor;
        this.t1 = Long.parseLong(config.getT1());
        this.t3 = Long.parseLong(config.getT3());
    }

    /**
     * 发送链路启动帧
     * <p>
     * 如果当前没有正在进行的启动任务，则发送STARTDT_ACT帧并启动超时检测任务
     * 超时时间为15秒，超时后将自动关闭连接
     */
    public void sendStartFrame() {

        log.info("发送 {} 启动帧", ByteBufUtil.hexDump(IEC104_BasicInstructions.STARTDT_ACT));

        IEC104_FrameBuilder frameBuilder = new IEC104_FrameBuilder.Builder(
                new IEC104_ApciMessageDetail(IEC104_BasicInstructions.STARTDT_ACT.getShort(0), IEC104_BasicInstructions.STARTDT_ACT.getShort(2)))
                .build();

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
        }, t1, TimeUnit.SECONDS);

        if (!startTask.compareAndSet(null, newTask)) {
            IEC104Util.isCancel(newTask);
            return;
        }

        // 更新 holder 为当前线程新建的任务
        startHolder.set(newTask);

        // 获取新创建的任务
        ScheduledFuture<?> self = startHolder.get();
        // 如果 startTask 等于 self 则将 startTask 更新为 null
        if (self != null && startTask.compareAndSet(self, null)) {
            ctx.write(frameBuilder);
        }
    }

    /**
     * 取消启动持有者线程的任务
     */
    public void onReceiveStartDTCon() {
        // 获取持有者任务
        ScheduledFuture<?> self = startHolder.get();
//        log.debug("task cancel: startHolder.get()={}, id={}", self, self == null ? null : System.identityHashCode(self));
        // ScheduledFuture<?> current = startTask.get();
        // log.debug("task run: t2Task.get()={}, id={}", current, current == null ? null : System.identityHashCode(current));

        // 检查引用相等
        // log.debug("引用相等? {}", self == current);

        IEC104Util.isCancel(self);
    }

    public void sendTestFrame() {

        ScheduledFuture<?> newTask = executor.schedule(() -> {
            log.info("发送 {} 测试帧", ByteBufUtil.hexDump(IEC104_BasicInstructions.TESTFR_ACT));
            // 经过 T3 时间没有报文交互，发送 TESTFR_ACT U测试帧
            IEC104_BasicInstructions.TESTFR_ACT.retain();
            ByteBufAllocator allocator = ctx.alloc();
            CompositeByteBuf compositeByteBuf = allocator.compositeBuffer();
            ByteBuf frame = allocator.buffer(6)
                    .writeByte(0x68)
                    .writeByte(0x04)
                    .writeShort(IEC104_BasicInstructions.TESTFR_ACT.getShort(0))
                    .writeShort(IEC104_BasicInstructions.TESTFR_ACT.getShort(2));
            compositeByteBuf.addComponent(frame);
            compositeByteBuf.writerIndex(compositeByteBuf.capacity());
            ScheduledFuture<?> self = testHolder.get();
            if (self != null && t3Task.compareAndSet(self, null)) {
                ctx.writeAndFlush(compositeByteBuf);
            }
            parent.startT1Timer();
        }, t3, TimeUnit.SECONDS);

        if (!t3Task.compareAndSet(null, newTask)) {
            IEC104Util.isCancel(newTask);
            return;
        }
//        log.debug("task run: testHolder.get()={}, id={}", newTask, newTask == null ? null : System.identityHashCode(newTask));
        testHolder.set(newTask);
    }

    /**
     * 取消测试持有者任务
     */
    public void onReceiveTestFRCon() {
        ScheduledFuture<?> self = testHolder.get();
        if (self != null && t3Task.compareAndSet(self, null)) {
//            log.debug("task cancel: testHolder.get()={}, id={}", self, self == null ? null : System.identityHashCode(self));
            IEC104Util.isCancel(self);
        }
    }
}
