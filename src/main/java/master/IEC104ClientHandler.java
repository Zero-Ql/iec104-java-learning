package master;

import IEC104Frameformat.ApduMessageDetail;
import core.ScheduledTaskPool;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class IEC104ClientHandler extends SimpleChannelInboundHandler<ApduMessageDetail> {

    /**
     * 在连接建立后调用该方法
     *
     * @param ctx 通道上下文对象
     * @throws Exception 声明 Exception 异常
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // 创建一个与当前通道处理器上下文绑定的 ScheduledTaskPool 实例
        ScheduledTaskPool.bindToChannel(ctx);
        // 获取 ScheduledTaskPool 并调用 sendStartFrame() 发送启动帧
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
