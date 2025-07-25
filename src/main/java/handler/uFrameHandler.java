package handler;

import IEC104Frameformat.FrameParser;
import enums.IEC104UFrameType;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.log4j.Log4j2;
import util.ByteUtil;
import util.IEC104Util;

@Log4j2
public abstract class uFrameHandler extends ChannelHandlerAdapter {
    @Override
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
        if (FrameParser.getFrameLength(bytes, 4) != 0x4) return false;
        // 指定为 U帧
        if (FrameParser.isFrameStart(bytes[0])) return false;
        // 控制域第0、1bit为 11
        return (bytes[2] & 0x003) == 0x003;
    }

    public abstract void uInstructionHandler(ChannelHandlerContext ctx, IEC104UFrameType uFrameType);
}
