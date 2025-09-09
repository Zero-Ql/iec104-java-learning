package core.codec;

import handler.IEC104_checkTheDataHandler;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class IEC104_Decoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        if (IEC104_checkTheDataHandler.getFrameLength(byteBuf.array(), 4) < 0) {
            return;
        }
        // 存档
        byteBuf.markReaderIndex();
        byte startByte = byteBuf.readByte();
        if (IEC104_checkTheDataHandler.isFrameStart(startByte)) {
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
//        IEC104_AsduMessageDetail asdu = new IEC104_AsduMessageDetail(typeId, vsq, coa, ioa, value);
//        list.add(asdu);
    }
}
