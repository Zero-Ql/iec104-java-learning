package core.codec;

import core.scheduler.IEC104_ScheduledTaskPool;
import frame.IEC104_FrameBuilder;
import frame.IEC104_MessageInfo;
import frame.apci.IEC104_ApciMessageDetail;
import frame.asdu.IEC104_AsduMessageDetail;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.CompositeByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.log4j.Log4j2;
import util.ByteUtil;

import java.util.List;

@Log4j2
public class IEC104_Encoder extends MessageToByteEncoder<IEC104_FrameBuilder> {

    private static final byte START_BYTE = 0x68;

    @Override
    protected void encode(ChannelHandlerContext ctx, IEC104_FrameBuilder frame, ByteBuf out) throws Exception {

        ByteBufAllocator allocator = ctx.alloc();
        CompositeByteBuf composite = allocator.compositeBuffer();
        // 编码 APCI
        ByteBuf apciBuf = encodeApci(allocator, frame.getApciMessageDetail());
        composite.addComponent(apciBuf);

        // 编码 ASDU（如果有）
        if (frame.getAsduMessageDetail() != null) {
            ByteBuf asduBuf = encodeAsdu(allocator, frame.getAsduMessageDetail());
            composite.addComponent(asduBuf);

        }

        int len = composite.capacity();

        ByteBuf header = allocator.buffer(2).writeByte(START_BYTE).writeByte(len);

        composite.addComponent(0, header);

        composite.writerIndex(composite.capacity());

        log.info("编码 IEC104 frame: {}", ByteBufUtil.hexDump(composite));

        ctx.writeAndFlush(composite);


    }

    private ByteBuf encodeApci(ByteBufAllocator allocator, IEC104_ApciMessageDetail apci) {
        ByteBuf buf = allocator.buffer(4);
        buf.writeShort(apci.getSendOrdinal());
        buf.writeShort(apci.getRecvOrdinal());
        return buf;
    }

    private ByteBuf encodeAsdu(ByteBufAllocator allocator, IEC104_AsduMessageDetail asdu) {
        ByteBuf buffer = allocator.buffer();
        boolean isContinuous = (asdu.getVariableStructureQualifiers() & 0x80) != 0;
        List<IEC104_MessageInfo> ioaList = asdu.getIOA();

        // 写入 ASDU 头部
        buffer.writeByte(asdu.getTypeIdentifier());
        buffer.writeByte(asdu.getVariableStructureQualifiers());
        buffer.writeByte(asdu.getTransferReason());
        buffer.writeByte(asdu.getSenderAddress());

        byte[] publicAddrBytes = ByteUtil.shortToByte(asdu.getPublicAddress());
        buffer.writeByte(publicAddrBytes[1]); // 小端序
        buffer.writeByte(publicAddrBytes[0]);

        if (isContinuous) {
            for (IEC104_MessageInfo info : ioaList) {
                buffer.writeMedium(info.getMessageAddress());
                buffer.writeBytes(info.getValue());
                buffer.writeByte(info.getQualityDescriptors());
            }
        } else {
            try {
                buffer.writeMedium(ioaList.getFirst().getMessageAddress());
                for (IEC104_MessageInfo info : ioaList) {
                    if (info.getValue() != null)
                        buffer.writeBytes(info.getValue());
                    buffer.writeByte(info.getQualityDescriptors());
                }
            } catch (Exception e) {
                log.error(e);
            }
        }

        return buffer;
    }
}