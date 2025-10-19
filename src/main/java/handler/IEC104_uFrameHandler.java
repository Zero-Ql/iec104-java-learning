package handler;

import common.IEC104_BasicInstructions;
import core.scheduler.IEC104_ScheduledTaskPool;
import frame.apci.event.UFrameEvent;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class IEC104_uFrameHandler extends ChannelHandlerAdapter {
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof UFrameEvent){
            UFrameEvent e = (UFrameEvent) evt;
            if (!e.isStart() && e.isStart_con()){
                IEC104_ScheduledTaskPool.getFromChannel(ctx).onReceiveStartDTCon();
                // 发送总召
                IEC104_ScheduledTaskPool.getFromChannel(ctx).sendInterrogationCommand();
                // 取消T1，重置T3
                IEC104_ScheduledTaskPool.getFromChannel(ctx).onReceiveTestFRCon();
            }
            if (!e.isTest() && e.isTest_con()){
                // 取消T1，重置T3
                IEC104_ScheduledTaskPool.getFromChannel(ctx).onReceiveTestFRCon();
            }
            if (!e.isStop() && e.isStop_con()){
                // 停止确认
               ByteBuf result = IEC104_BasicInstructions.STOPDT_CON;
            }
        }
    }
}
