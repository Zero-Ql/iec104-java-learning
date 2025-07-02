package master;

import IEC104Frameformat.ASDUParser;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class IEC104ClientHandler extends SimpleChannelInboundHandler<ASDUParser> {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }

    @Override
    protected void messageReceived(ChannelHandlerContext channelHandlerContext, ASDUParser asduParser) throws Exception {
        asduParser.toString();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
