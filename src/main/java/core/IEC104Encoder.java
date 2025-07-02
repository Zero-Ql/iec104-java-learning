package core;

import IEC104Frameformat.AsduMessageDetail;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class IEC104Encoder extends MessageToByteEncoder<AsduMessageDetail> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, AsduMessageDetail asduMessageDetail, ByteBuf byteBuf) throws Exception {
        int asduLength = 8;
        int apciLength = 2 + 4;
        int apduLength = apciLength + asduLength;

        byteBuf.writeByte(0x68);
        byteBuf.writeByte(apduLength);

        byteBuf.writeByte(0x00);
        byteBuf.writeByte(0x00);
        byteBuf.writeByte(0x00);
        byteBuf.writeByte(0x00);


        byteBuf.writeByte(asduMessageDetail.getTypeIdentifier());
        byteBuf.writeByte(asduMessageDetail.getVariableStructureQualified());
        byteBuf.writeShort(asduMessageDetail.getCommonAddress());
        byteBuf.writeInt(asduMessageDetail.getIoAddress());
        byteBuf.writeFloat(asduMessageDetail.getValue());
    }
}
