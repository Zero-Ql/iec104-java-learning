package slave;

import Frameformat.IEC104_AsduMessageDetail;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class IEC104_ServerHandler extends SimpleChannelInboundHandler<IEC104_AsduMessageDetail> {

    @Override
    protected void messageReceived(ChannelHandlerContext channelHandlerContext, IEC104_AsduMessageDetail IEC104AsduMessageDetail) throws Exception {
        IEC104AsduMessageDetail.toString();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
