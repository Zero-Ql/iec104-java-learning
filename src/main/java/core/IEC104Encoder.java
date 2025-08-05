package core;

import IEC104Frameformat.AsduMessageDetail;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class IEC104Encoder extends MessageToByteEncoder<AsduMessageDetail> {
    /**
     * 将AsduMessageDetail对象编码为IEC104协议格式的字节流
     * 
     * @param channelHandlerContext 通道处理上下文
     * @param asduMessageDetail ASDU消息详情对象
     * @param byteBuf 用于写入编码后字节的缓冲区
     * @throws Exception 编码过程中可能抛出的异常
     */
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


//        byteBuf.writeByte(asduMessageDetail.getTypeIdentifier());
//        byteBuf.writeByte(asduMessageDetail.getVariableStructureQualified());
//        byteBuf.writeShort(asduMessageDetail.getCommonAddress());
//        byteBuf.writeInt(asduMessageDetail.getIoAddress());
//        byteBuf.writeFloat(asduMessageDetail.getValue());
    }
}
