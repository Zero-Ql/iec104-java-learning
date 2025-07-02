package core;

import IEC104Frameformat.ASDUParser;
import IEC104Frameformat.FrameParser;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class IEC104Decoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        if (FrameParser.getFrameLength(byteBuf.array(), 4) < 0) {
            return;
        }
        // 存档
        byteBuf.markReaderIndex();
        byte startByte = byteBuf.readByte();
        if (FrameParser.isFrameStart(startByte)) {
            byteBuf.resetReaderIndex(); // 回档
            return;
        }
        int apduLength = byteBuf.readUnsignedByte();
        // 判断数据是否完整
        if (byteBuf.readableBytes() < apduLength) {
            byteBuf.resetReaderIndex();
            return;
        }
        // 读取控制域
        byte[] controlField = new byte[4];
        byteBuf.readBytes(controlField);

        byte typeId = byteBuf.readByte();
        byte vsq = byteBuf.readByte();
        short coa = byteBuf.readShort();
        int ioa = byteBuf.readInt();
        float value = byteBuf.readFloat();
        ASDUParser asdu = new ASDUParser(typeId, vsq, coa, ioa, value);
        list.add(asdu);
    }
}
