package master.handler;

import common.IEC104BasicInstructions;
import enums.IEC104UFrameType;
import handler.uFrameHandler;
import io.netty.channel.ChannelHandlerContext;

public class uFrameMasterHandler extends uFrameHandler {
    @Override
    public void uInstructionHandler(ChannelHandlerContext ctx, IEC104UFrameType uFrameType) {
        byte [] result = null;
        switch (uFrameType) {
            case STARTDT_CON:
                result = IEC104BasicInstructions.STARTDT_CON;
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
