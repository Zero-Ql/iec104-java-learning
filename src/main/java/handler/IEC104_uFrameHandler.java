package handler;

import Frameformat.IEC104_FrameParser;
import enums.IEC104_UFrameType;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.log4j.Log4j2;
import util.ByteUtil;
import util.IEC104Util;

@Log4j2
public abstract class IEC104_uFrameHandler extends ChannelHandlerAdapter {
    /**
     * 处理通道读取事件
     * <p>
     * 该方法用于解析接收到的数据，判断是否为U帧，如果是则进行相应处理，否则将数据传递给下一个处理器
     *
     * @param ctx 通道处理上下文
     * @param msg 接收到的消息对象
     * @throws Exception 处理过程中可能抛出的异常
     */
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        var result = (ByteBuf) msg;
        // 存档
        result.markReaderIndex();
        var bytes = new byte[6];
        // 只读前 6 个字节

        result.readBytes(bytes);
        // 判断是否为 U帧
        if (isUFrame(bytes)) {
            // 截取控制域的 4 个字节
            var uControlType = IEC104Util.getUControlType(ByteUtil.subBytes(bytes, 2, 4));
            if (uControlType != null) {
                // 由子类实现
                uInstructionHandler(ctx, uControlType);
                // 已处理该帧，不再向下传递，直接返回
                return;
            }
        }
        // 非 U帧回滚后原样传给下个处理器
        result.resetReaderIndex();
        // 继续让后续处理器处理
        ctx.fireChannelRead(result);
    }

    private boolean isUFrame(byte[] bytes) {
        // 长度域必须为 4
        if (IEC104_checkTheDataHandler.getFrameLength(bytes, 4) != 0x4) return false;
        // 指定为 U帧
        if (IEC104_checkTheDataHandler.isFrameStart(bytes[0])) return false;
        // 控制域第0、1bit为 11
        return (bytes[2] & 0x003) == 0x003;
    }

    public abstract void uInstructionHandler(ChannelHandlerContext ctx, IEC104_UFrameType uFrameType);
}
