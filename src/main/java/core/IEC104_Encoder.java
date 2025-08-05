package core;

import Frameformat.IEC104_AsduMessageDetail;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class IEC104_Encoder extends MessageToByteEncoder<IEC104_AsduMessageDetail> {
    /**
     * 将AsduMessageDetail对象编码为IEC104协议格式的字节流
     * 
     * @param channelHandlerContext 通道处理上下文
     * @param IEC104AsduMessageDetail ASDU消息详情对象
     * @param byteBuf 用于写入编码后字节的缓冲区
     * @throws Exception 编码过程中可能抛出的异常
     */
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, IEC104_AsduMessageDetail IEC104AsduMessageDetail, ByteBuf byteBuf) throws Exception {
        int asduLength = 8;
        int apciLength = 2 + 4;
        int apduLength = apciLength + asduLength;

        byteBuf.writeByte(0x68);
        byteBuf.writeByte(apduLength);

        byteBuf.writeByte(0x00);
        byteBuf.writeByte(0x00);
        byteBuf.writeByte(0x00);
        byteBuf.writeByte(0x00);


//        byteBuf.writeByte(IEC104AsduMessageDetail.getTypeIdentifier());
//        byteBuf.writeByte(IEC104AsduMessageDetail.getVariableStructureQualified());
//        byteBuf.writeShort(IEC104AsduMessageDetail.getCommonAddress());
//        byteBuf.writeInt(IEC104AsduMessageDetail.getIoAddress());
//        byteBuf.writeFloat(IEC104AsduMessageDetail.getValue());
    }
}
