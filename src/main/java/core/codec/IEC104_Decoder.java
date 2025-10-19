package core.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class IEC104_Decoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        // 判断可读数据长度
        if ( byteBuf.readableBytes() < 2)return;
        byteBuf.markReaderIndex();
        // 根据 帧头拆帧
        if (byteBuf.readUnsignedByte() != 0x68){
            // 重置指针
            byteBuf.resetReaderIndex();
            return;
        }

        int apduLength = byteBuf.readUnsignedByte();
        // 当前可读数据长度小于apdu长度，则重置指针
        if (byteBuf.readableBytes() < apduLength) {
            byteBuf.resetReaderIndex();
            return;
        }

        list.add(byteBuf.readBytes(apduLength));
    }
}
