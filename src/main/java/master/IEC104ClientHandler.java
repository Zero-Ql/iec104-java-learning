package master;

import IEC104Frameformat.ApduMessageDetail;
import core.ScheduledTaskPool;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class IEC104ClientHandler extends SimpleChannelInboundHandler<ApduMessageDetail> {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // 创建一个与当前连接绑定的 ScheduledTaskPool 实例，并存入线程本地变量
        ScheduledTaskPool.bindToChannel(ctx);
        // 发送开始帧
        ScheduledTaskPool.getFromChannel(ctx).sendStartFrame();

    }

    @Override
    protected void messageReceived(ChannelHandlerContext channelHandlerContext, ApduMessageDetail apduMessageDetail) throws Exception {
        apduMessageDetail.toString();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ScheduledTaskPool pool = ScheduledTaskPool.getFromChannel(ctx);
        if (pool != null) pool.shutdown();
        ctx.close();
    }
}
