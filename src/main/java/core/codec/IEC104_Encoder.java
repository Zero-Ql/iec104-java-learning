package core.codec;

import core.scheduler.IEC104_ScheduledTaskPool;
import frame.IEC104_FrameBuilder;
import frame.IEC104_MessageInfo;
import frame.apci.IEC104_ApciMessageDetail;
import frame.asdu.IEC104_AsduMessageDetail;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.CompositeByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import util.ByteUtil;

import java.util.List;

public class IEC104_Encoder extends MessageToByteEncoder<IEC104_FrameBuilder> {

    /**
     * 起始字节 固定 一字节
     */
    private static final byte start = 0x68;

    /**
     * 将 IEC104_FrameBuilder 对象编码为IEC104协议格式的字节流
     *
     * @param ctx                 通道处理上下文
     * @param iEC104_FrameBuilder IEC104帧对象
     * @param byteBuf             用于写入编码后字节的缓冲区
     * @throws Exception 编码过程中可能抛出的异常
     */
    @Override
    protected void encode(ChannelHandlerContext ctx, IEC104_FrameBuilder iEC104_FrameBuilder, ByteBuf byteBuf) throws Exception {
        ByteBufAllocator allocator = ctx.alloc();

        // 使用 CompositeByteBuf 拼接
        CompositeByteBuf composite = allocator.compositeBuffer();

        // APCI 字段（控制字段为 4 字节）
        ByteBuf apcibuf = allocator.buffer(4);

        IEC104_ApciMessageDetail apciMessageDetail = iEC104_FrameBuilder.getApciMessageDetail();
        apcibuf.writeShort(apciMessageDetail.getSendOrdinal());
        apcibuf.writeShort(apciMessageDetail.getRecvOrdinal());

        composite.addComponent(apcibuf);

        if (iEC104_FrameBuilder.getAsduMessageDetail() != null) {

            IEC104_AsduMessageDetail asduMessageDetail = iEC104_FrameBuilder.getAsduMessageDetail();

            List<IEC104_MessageInfo> IOA = asduMessageDetail.getIOA();

            // 将 IOA 列表序列化为字节流

            // ASDU 头部字段（4 字节：Type ID + VSQ + Transfer Reason + Sender Address）
            // 公共地址（2 字节：Public Address 2 字节）
            // IOA 地址（3 字节：使用 writeMedium 写入 3 字节整数）
            ByteBuf asdu = allocator.buffer(9);

            boolean isContinuous = (asduMessageDetail.getVariableStructureQualifiers() & 0x80) != 0;

            // 值 & 质量码（可变长度）
            ByteBuf value = allocator.buffer();

            asdu.writeByte(asduMessageDetail.getTypeIdentifier());
            asdu.writeByte(asduMessageDetail.getVariableStructureQualifiers());
            asdu.writeByte(asduMessageDetail.getTransferReason());
            asdu.writeByte(asduMessageDetail.getSenderAddress());

            byte[] byteAddress = ByteUtil.shortToByte(asduMessageDetail.getPublicAddress());
            // 将 short 转为字节数组后转为 小端序 写入
            asdu.writeByte(byteAddress[1]);
            asdu.writeByte(byteAddress[0]);

            // true 连续; false 不连续
            if (isContinuous) {
                IOA.forEach(info -> {
                    asdu.writeMedium(info.getMessageAddress());
                    value.writeBytes(info.getValue());
                    value.writeByte(info.getQualityDescriptors());
                });
            } else {
                asdu.writeMedium(IOA.getFirst().getMessageAddress());
                IOA.forEach(info -> {
                    value.writeBytes(info.getValue());
                    value.writeByte(info.getQualityDescriptors());
                });
            }

            composite.addComponent(asdu);
            composite.addComponent(value);
        }

//        composite.writerIndex(composite.capacity());

        ctx.write(composite, ctx.voidPromise());

        // 启动 T1 定时器
        IEC104_ScheduledTaskPool.getFromChannel(ctx).startT1Timer();
    }
}
