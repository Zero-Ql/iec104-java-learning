package master.handler;

import handler.IEC104_iFrameHandler;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public class IEC104_iFrameMasterHandler extends IEC104_iFrameHandler {
    @Override
    public void iInstructionHandler(ChannelHandlerContext ctx, ByteBuf payload) {

    }
}
