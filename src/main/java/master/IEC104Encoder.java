package master;

import IEC104Frameformat.ASDUParser;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class IEC104Encoder extends MessageToByteEncoder<ASDUParser> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, ASDUParser asduParser, ByteBuf byteBuf) throws Exception {
        int asduLength = 8;
        int apciLength = 2 + 4;
        int apduLength = apciLength + asduLength;

        byteBuf.writeByte(0x68);
        byteBuf.writeByte(apduLength);

        byteBuf.writeByte(0x00);
        byteBuf.writeByte(0x00);
        byteBuf.writeByte(0x00);
        byteBuf.writeByte(0x00);


        byteBuf.writeByte(asduParser.getTypeIdentifier());
        byteBuf.writeByte(asduParser.getVariableStructureQualified());
        byteBuf.writeShort(asduParser.getCommonAddress());
        byteBuf.writeInt(asduParser.getIoAddress());
        byteBuf.writeFloat(asduParser.getValue());
    }
}
