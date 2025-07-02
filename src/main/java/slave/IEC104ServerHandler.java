package slave;

import IEC104Frameformat.AsduMessageDetail;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class IEC104ServerHandler extends SimpleChannelInboundHandler<AsduMessageDetail> {

    @Override
    protected void messageReceived(ChannelHandlerContext channelHandlerContext, AsduMessageDetail asduMessageDetail) throws Exception {
        asduMessageDetail.toString();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
