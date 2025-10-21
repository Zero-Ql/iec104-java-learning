package handler;

import frame.apci.event.UFrameEvent;
import frame.asdu.IEC104_AsduMessageDetail;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.log4j.Log4j2;
import util.ByteUtil;
import util.IEC104Util;

@Log4j2
public class IEC104_SeqManager extends ChannelHandlerAdapter {

    private short nextRx = 0;
    private short nextTx = 0;
    private short lastAck = 0;

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

        if (!(msg instanceof ByteBuf)) {
            return;
        }

        ByteBuf frame = (ByteBuf) msg;

        try {
            byte flag = frame.getByte(2);
            // 判断是否为U帧
            if (ByteUtil.isTypeU(flag)) {
                byte type = frame.getByte(2);
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
                int sendTx = frame.getUnsignedShort(2) & 0x7FFF;
                int recvRx = frame.getUnsignedShort(4) & 0x7FFF;

                if (sendTx != nextRx) {
                    ctx.close();
                    log.error("接收到的I帧序号与发送序号不一致，关闭通道");
                    return;
                }

                // 接收到的I帧序号加1
                nextRx = (short) ((nextRx + 1) & 0x7FFF);

                // 如果接收序号大于上次确认序号，则更新
                if (recvRx > lastAck) lastAck = (short) recvRx;

                IEC104_AsduMessageDetail asduMessageDetail = IEC104Util.decodeAsdu(
                        frame.slice(6, frame.readableBytes() - 6));

                // 传递I帧asdu给下一个处理器
                ctx.fireChannelRead(asduMessageDetail);
            }
        } finally {
            frame.release();
        }
    }
}
