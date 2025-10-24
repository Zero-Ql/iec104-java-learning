package common;

import config.Piec104Config;
import frame.IEC104_FrameBuilder;
import frame.apci.IEC104_ApciMessageDetail;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import util.IEC104Util;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class IEC104_SFrameTaskManager {
    private final long t2;
    private final int current_w;
    private final ChannelHandlerContext ctx;
    private final ScheduledExecutorService executor;
    private final AtomicInteger w = new AtomicInteger(0);
    private final AtomicInteger latestRecvSeq = new AtomicInteger(0);
    private final AtomicReference<ScheduledFuture<?>> t2Task = new AtomicReference<>();


    private static final Logger log = LogManager.getLogger(IEC104_SFrameTaskManager.class);

    public IEC104_SFrameTaskManager(ChannelHandlerContext ctx, ScheduledExecutorService executor, Piec104Config config) {
        this.ctx = ctx;
        this.executor = executor;
        this.current_w = Integer.parseInt(config.getW());
        this.t2 = Long.parseLong(config.getT2());
    }

    public void sendSFrame(short recvOrdinal) {
        latestRecvSeq.set(recvOrdinal);

        ScheduledFuture<?> task = t2Task.getAndSet(null);
        IEC104Util.isCancel(task);

        // 如果 w 等于本地确认序号则立即发送 S 帧，否则开启 t2 计时器
        if (w.getAndIncrement() == current_w) {
            sendSFrameNow((short) latestRecvSeq.get());
            w.set(0);
        } else {
            ScheduledFuture<?> newTask = executor.schedule(() -> sendSFrameNow((short) latestRecvSeq.get()), t2, TimeUnit.SECONDS);
            t2Task.compareAndSet(task, newTask);
        }
    }

    private void sendSFrameNow(short recvOrdinal) {
        IEC104_ApciMessageDetail apciMessageDetail = new IEC104_ApciMessageDetail((short) 0x1, recvOrdinal);
        IEC104_FrameBuilder frameBuilder = new IEC104_FrameBuilder.Builder(apciMessageDetail).build();
        log.info("发送 {} S 帧", frameBuilder);
        // 将 S 帧数据发送至缓冲区
        ctx.write(frameBuilder);
    }
}
