package common;

import config.Piec104Config;
import handler.IEC104_SeqManager;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import util.IEC104Util;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class IEC104_SFrameTaskManager {
    private final ChannelHandlerContext ctx;
    private final ScheduledExecutorService executor;
    private final Piec104Config config;
    private final AtomicReference<ScheduledFuture<?>> t2Task = new AtomicReference<>();

    private static final Logger log = LogManager.getLogger(IEC104_SFrameTaskManager.class);

    public IEC104_SFrameTaskManager(ChannelHandlerContext ctx, ScheduledExecutorService executor, Piec104Config config) {
        this.ctx = ctx;
        this.executor = executor;
        this.config = config;
    }

    public void sendSFrame(short recvOrdinal) {
        short w = Short.parseShort(config.getW());
        short local_w = 0;
        ScheduledFuture<?> task = t2Task.get();
        IEC104Util.isCancel(task);
        ScheduledFuture<?> newTask = executor.schedule(() -> {
            if (local_w == w) {
                log.info("发送 {} S 帧", recvOrdinal);
                ctx.
            }
        }, Long.parseLong(config.getT2()), TimeUnit.SECONDS);
    }
}
