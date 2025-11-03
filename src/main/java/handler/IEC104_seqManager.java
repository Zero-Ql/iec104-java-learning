package handler;

import frame.apci.event.UFrameEvent;
import frame.asdu.IEC104_AsduMessageDetail;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.log4j.Log4j2;
import util.ByteUtil;
import util.IEC104Util;

import java.util.concurrent.atomic.AtomicInteger;

@Log4j2
public abstract class IEC104_seqManager extends ChannelHandlerAdapter {

    // 本地接收序号
    protected final AtomicInteger recvOrdinal = new AtomicInteger(0);
    // 本地发送序号
    protected final AtomicInteger sendOrdinal = new AtomicInteger(0);
    // 上一个确认序号
    protected final AtomicInteger lastAck = new AtomicInteger(0);

    /**
     * 处理通道读取事件
     * <p>
     * 该方法用于解析接收到的数据，判断是否为I帧，如果是则进行相应处理，否则将数据传递给下一个处理器
     *
     * @param ctx 通道处理上下文
     * @param msg 接收到的消息对象
     * @throws Exception 处理过程中可能抛出的异常
     */
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        if (!(msg instanceof ByteBuf frame)) {
            return;
        }

        try {
            byte flag = frame.getByte(0);
            // 判断是否为U帧
            if (ByteUtil.isTypeU(flag)) {
                byte type = frame.readByte();
                // 初始化U帧类型
                boolean test = (type & 0x43) == 0x43;
                boolean start = (type & 0x07) == 0x07;
                boolean stop = (type & 0x13) == 0x13;
                boolean test_con = (type & 0x83) == 0x83;
                boolean start_con = (type & 0x0B) == 0x0B;
                boolean stop_con = (type & 0x23) == 0x23;
                // 传递U帧事件给下一个处理器
                ctx.fireUserEventTriggered(UFrameEvent.of(
                        type,
                        test,
                        start,
                        stop,
                        test_con,
                        start_con,
                        stop_con));
            }

            if (ByteUtil.isTypeI(flag)) {

                int sendOrdinal = Short.reverseBytes((short) (frame.readUnsignedShort() & 0x7FFF));
                int recvOrdinal = Short.reverseBytes((short) (frame.readUnsignedShort() & 0x7FFF));

                if (sendOrdinal != this.recvOrdinal.get()) {
                    log.error("接收到的I帧发送序号与本地接收序号不一致，关闭通道");
                    ctx.close();
                    return;
                }

                // 本地接收序号加2(如果超过 32767 则用 & 0x7FFF 清零)
                this.recvOrdinal.set((short) ((this.recvOrdinal.get() + 2) & 0x7FFF));

                customTasks(ctx, recvOrdinal);

                IEC104_AsduMessageDetail asduMessageDetail = IEC104Util.decodeAsdu(frame.readBytes(frame.readableBytes()));

                // 传递I帧asdu给下一个处理器
                ctx.fireChannelRead(asduMessageDetail);
            }
        } finally {
            frame.release();
        }
    }

    public abstract void customTasks(ChannelHandlerContext ctx, int recvOrdinal);
}
