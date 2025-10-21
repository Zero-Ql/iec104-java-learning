package master;

import core.scheduler.IEC104_ScheduledTaskPool;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class IEC104_ClientHandler extends ChannelHandlerAdapter {
    private static final Logger log = LogManager.getLogger(IEC104_ClientHandler.class);

    /**
     * 在连接建立后调用该方法
     *
     * @param ctx 通道上下文对象
     * @throws Exception 声明 Exception 异常
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("连接已建立");
        // 创建一个与当前通道处理器上下文绑定的 IEC104_ScheduledTaskPool 实例
        IEC104_ScheduledTaskPool.bindToChannel(ctx);
        // 获取 IEC104_ScheduledTaskPool 并调用 sendStartFrame() 发送启动帧
        IEC104_ScheduledTaskPool.getFromChannel(ctx).sendStartFrame();
        IEC104_ScheduledTaskPool.getFromChannel(ctx).sendTestFrame();
    }

    /**
     * 处理通道非活跃状态
     * <p>
     * 当通道断开连接时调用此方法，记录连接断开的日志
     *
     * @param ctx 通道处理上下文
     * @throws Exception 状态处理过程中可能抛出的异常
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.warn("连接已断开");
        super.channelInactive(ctx);
    }

    /**
     * 处理异常情况
     * <p>
     * 当通道处理过程中发生异常时调用此方法，记录异常信息并关闭相关资源
     *
     * @param ctx   通道处理上下文
     * @param cause 异常对象
     * @throws Exception 异常处理过程中可能抛出的异常
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("发生异常：", cause);
        IEC104_ScheduledTaskPool pool = IEC104_ScheduledTaskPool.getFromChannel(ctx);
        if (pool != null) pool.shutdown();
        ctx.close();
    }
}
