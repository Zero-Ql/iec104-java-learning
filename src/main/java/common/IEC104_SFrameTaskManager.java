package common;

import config.Piec104Config;
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
    private final AtomicReference<ScheduledFuture<?>> holder = new AtomicReference<>();  // 标识当前任务持有者
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
     * 发送 S 帧（确认帧）的方法。根据接收序号更新本地状态，并决定是否立即发送 S 帧或延迟发送。
     *
     * @param recvOrdinal 接收到的最新帧的序号，用于构造 S 帧中的确认序号字段
     */
    public void sendSFrame(short recvOrdinal) {

        latestRecvSeq.set(recvOrdinal);

        // 如果 w 等于本地确认序号则立即发送 S 帧，否则开启 t2 计时器
        if (w.getAndIncrement() >= current_w) {
            ScheduledFuture<?> task = t2Task.getAndSet(null);
            if (task != null && !task.isDone()) {
                IEC104Util.isCancel(task);
            }
            log.debug("当前发送S帧方法：{} >= {}", w.get() - 1, current_w);
            sendSFrameNow();
            return;
        }

        ScheduledFuture<?> newTask = executor.schedule(() -> {
            // 获取新创建的任务
            ScheduledFuture<?> self = holder.get();
            // 如果 t2Task 等于 self 则将 t2Task 更新为 null
            if (self != null && t2Task.compareAndSet(self, null)) {
                sendSFrameNow();
            }
        }, t2, TimeUnit.SECONDS);

        // 如果 t2Task 不为 null 则取消新创建的任务并返回(防止holder指向被取消的任务)
        if (!t2Task.compareAndSet(null, newTask)) {
            IEC104Util.isCancel(newTask);
            return;
        }
        // 更新 holder 为当前线程新建的任务
        holder.set(newTask);
    }

    /**
     * 立即构造并发送一个 S 帧（确认帧），将当前最新的接收序号作为确认信息发送出去。
     * 同时重置计数器 w 并记录日志。
     */
    private void sendSFrameNow() {
        w.set(0);
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
            if (f.isSuccess()) {
                log.info("S帧发送成功, 重置S帧");
            }
        });
    }

}

