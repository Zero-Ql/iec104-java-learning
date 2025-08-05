package master.handler;

import common.IEC104BasicInstructions;
import core.ScheduledTaskPool;
import enums.IEC104UFrameType;
import handler.uFrameHandler;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class uFrameMasterHandler extends uFrameHandler {
    /**
     * 处理U帧指令
     * <p>
     * 根据不同的U帧类型执行相应的操作，包括启动确认、停止确认和测试确认等
     *
     * @param ctx 通道处理上下文
     * @param uFrameType U帧类型枚举值
     */
    @Override
    public void uInstructionHandler(ChannelHandlerContext ctx, IEC104UFrameType uFrameType) {
        ByteBuf result = null;
        // 根据 u确认帧类型执行不同操作
        switch (uFrameType) {
            case STARTDT_CON:
                ScheduledTaskPool.getFromChannel(ctx).onReceiveStartDTCon();
                ScheduledTaskPool.getFromChannel(ctx).onReceiveTestFRCon();
                break;
            case STOPDT_CON:
                result = IEC104BasicInstructions.STOPDT_CON;
                break;
            case TESTFR_CON:
                ScheduledTaskPool.getFromChannel(ctx).onReceiveTestFRCon();
                break;
            default:
                log.error("U帧无效{}", uFrameType);
                break;
        }
    }
}
