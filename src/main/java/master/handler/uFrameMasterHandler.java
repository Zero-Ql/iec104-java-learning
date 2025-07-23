package master.handler;

import common.IEC104BasicInstructions;
import core.ScheduledTaskPool;
import enums.IEC104UFrameType;
import handler.uFrameHandler;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class uFrameMasterHandler extends uFrameHandler {
    @Override
    public void uInstructionHandler(ChannelHandlerContext ctx, IEC104UFrameType uFrameType) {
        byte[] result = null;
        // 根据 u确认帧类型执行不同操作
        switch (uFrameType) {
            case STARTDT_CON:
                ScheduledTaskPool.getFromChannel(ctx).onReceiveStartDTCon();
                break;
            case STOPDT_CON:
                result = IEC104BasicInstructions.STOPDT_CON;
                break;
            case TESTFR_CON:
                result = IEC104BasicInstructions.TESTFR_CON;
                break;
            default:
                log.error("U帧无效{}", uFrameType);
                break;
        }
    }
}
