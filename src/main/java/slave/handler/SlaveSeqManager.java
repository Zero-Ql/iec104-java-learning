package slave.handler;

import handler.IEC104_seqManager;
import io.netty.channel.ChannelHandlerContext;

public class SlaveSeqManager extends IEC104_seqManager {
    /**
     * 更新本地接收序号
     *
     * @param ctx         通道上下文
     * @param recvOrdinal 接收序号的原子引用
     */
    @Override
    public void customTasks(ChannelHandlerContext ctx, int recvOrdinal) {
        // 如果接收序号大于本地确认序号，则更新(用于子站确认主站的接收数)
        if (recvOrdinal > super.lastAck.get()) super.lastAck.set((short) recvOrdinal);
    }
}
