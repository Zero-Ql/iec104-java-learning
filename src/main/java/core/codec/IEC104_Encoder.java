package core.codec;

import frame.IEC104_FrameBuilder;
import frame.asdu.IEC104_AsduMessageDetail;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class IEC104_Encoder extends MessageToByteEncoder<IEC104_FrameBuilder> {

    /**
     * 起始字节 固定 一字节
     */
    private static final byte start = 0x68;

    /**
     * 将 IEC104_FrameBuilder 对象编码为IEC104协议格式的字节流
     *
     * @param ctx 通道处理上下文
     * @param iEC104_FrameBuilder   IEC104帧对象
     * @param byteBuf               用于写入编码后字节的缓冲区
     * @throws Exception 编码过程中可能抛出的异常
     */
    @Override
    protected void encode(ChannelHandlerContext ctx, IEC104_FrameBuilder iEC104_FrameBuilder, ByteBuf byteBuf) throws Exception {
        ByteBufAllocator allocator = ctx.alloc();
    }
}
