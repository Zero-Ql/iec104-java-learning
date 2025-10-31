package common;

import config.Piec104Config;
import frame.IEC104_FrameBuilder;
import frame.apci.IEC104_ApciMessageDetail;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.CompositeByteBuf;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import util.ByteUtil;
import util.IEC104Util;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * IEC104_SFrameTaskManager 类用于管理 IEC 104 协议中的 S 帧（确认帧）的发送任务。
 * 它根据接收序列号和窗口大小控制 S 帧的发送时机，并支持定时发送机制。
 */
public class IEC104_SFrameTaskManager {
    private final long t2;                         // T2 超时时间，单位秒，用于延迟发送 S 帧的时间间隔
    private final int current_w;                   // W 参数，表示最大未确认的 I 帧数量，超过该值需立即发送 S 帧
    private final ChannelHandlerContext ctx;       // Netty 的通道上下文，用于发送数据帧
    private final ScheduledExecutorService executor;// 定时任务执行器，用于调度 S 帧的延时发送任务
    private final AtomicInteger w = new AtomicInteger(0);              // 当前已接收到但尚未确认的 I 帧计数器
    private final AtomicInteger latestRecvSeq = new AtomicInteger(0);  // 最新接收到的接收序列号
    private final AtomicBoolean isSending = new AtomicBoolean(false);  // 标识当前是否正在发送 S 帧，防止并发发送
    private final AtomicReference<ScheduledFuture<?>> t2Task = new AtomicReference<>(); // 指向当前正在运行的 T2 定时任务

    private static final Logger log = LogManager.getLogger(IEC104_SFrameTaskManager.class);

    /**
     * 构造方法初始化 S 帧任务管理器的相关配置与依赖组件。
     *
     * @param ctx      Netty 的通道处理上下文，用于发送构建好的帧
     * @param executor 用于调度定时任务的线程池
     * @param config   包含协议相关参数（如 T2 和 W）的配置对象
     */
    public IEC104_SFrameTaskManager(ChannelHandlerContext ctx, ScheduledExecutorService executor, Piec104Config config) {
        this.ctx = ctx;
        this.executor = executor;
        this.current_w = Integer.parseInt(config.getW());
        this.t2 = Long.parseLong(config.getT2());
    }

    /**
     * 接收最新的接收序号并决定是否立即发送 S 帧或启动定时任务进行延时发送。
     * 若累计接收到的 I 帧数量达到设定窗口大小，则立即发送 S 帧；否则设置一个 T2 时间后自动发送。
     *
     * @param recvOrdinal 新接收到的接收序列号
     */
    public void sendSFrame(short recvOrdinal) {

        latestRecvSeq.set(recvOrdinal);

        ScheduledFuture<?> task = t2Task.getAndSet(null);

        // 如果 w 等于本地确认序号则立即发送 S 帧，否则开启 t2 计时器
        if (w.getAndIncrement() >= current_w) {
            if (task != null && !task.isDone()) {
                IEC104Util.isCancel(task);
            }
            sendSFrameNow();
        } else {
            if (task != null && !task.isDone()) {
                return;
            }
            ScheduledFuture<?> newTask = executor.schedule(this::sendSFrameNow, t2, TimeUnit.SECONDS);
            t2Task.compareAndSet(task, newTask);
        }
    }

    /**
     * 实际构造并发送 S 帧的方法。使用原子操作确保同一时刻只有一个 S 帧在发送中，
     */
    private void sendSFrameNow() {
        w.set(0);
        if (isSending.compareAndSet(false, true)) {
            ByteBufAllocator allocator = ctx.alloc();
            CompositeByteBuf compositeByteBuf = allocator.compositeBuffer();
            ByteBuf frame = allocator.buffer(6)
                    .writeByte(0x68)
                    .writeByte(0x04)
                    .writeShort(Short.reverseBytes((short) 0x01))
                    .writeShort(Short.reverseBytes((short) latestRecvSeq.get()));
            compositeByteBuf.addComponent(frame);
            compositeByteBuf.writerIndex(compositeByteBuf.capacity());
            log.info("发送 {} S 帧", ByteBufUtil.hexDump(compositeByteBuf));

            // 直接发送 S 帧数据
            ctx.writeAndFlush(compositeByteBuf).addListener(f -> {
                isSending.set(false);
                if (f.isSuccess()) {
                    log.info("S帧发送成功, 重置S帧");
                }
            });
        }
    }
}

