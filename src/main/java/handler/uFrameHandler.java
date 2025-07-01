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
        result.markReaderIndex();
        var bytes = new byte[6];
        result.readBytes(bytes);
        if (isUFrame(bytes)) {
            var uControlType = IEC104Util.getUControlType(ByteUtil.subBytes(bytes, 2, 4));
            if (uControlType != null) {
                uInstructionHandler(ctx, uControlType);
                return;
            }
        }
        result.resetReaderIndex();
        ctx.fireChannelRead(result);
    }

    private boolean isUFrame(byte[] bytes) {
        if (FrameParser.getFrameLength(bytes, 4) != 0x4) return false;
        if (FrameParser.isFrameStart(bytes[0])) return false;
        return (bytes[2] & 0x003) == 0x003;
    }

    public abstract void uInstructionHandler(ChannelHandlerContext ctx, IEC104UFrameType uFrameType);
}
