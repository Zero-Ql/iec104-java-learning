package master.handler;

import core.scheduler.IEC104_ScheduledTaskPool;
import handler.IEC104_seqManager;
import io.netty.channel.ChannelHandlerContext;

public class MasterSeqManager extends IEC104_seqManager {
    /**
     * 自定义任务
     * ChannelHandlerContext ctx 通道上下文
     * AtomicInteger recvOrdinal 本地接收序号的原子引用
     */
    @Override
    public void customTasks(ChannelHandlerContext ctx, int recvOrdinal) {
        // 发送 S 帧
        IEC104_ScheduledTaskPool.getFromChannel(ctx).sendSFrame((short) super.recvOrdinal.get());

    }
}
