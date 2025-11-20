/*
 * IEC 60870-5-104 Protocol Implementation
 * Copyright (C) 2025 QSky
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */
package impl.iec104.handler;

import impl.iec104.common.IEC104_BasicInstructions;
import impl.iec104.core.scheduler.IEC104_ScheduledTaskPool;
import impl.iec104.frame.apci.event.UFrameEvent;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class IEC104_uFrameHandler extends ChannelHandlerAdapter {
    /**
     * @param ctx 通道上下文
     * @param evt 用户自定义事件
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        if (evt instanceof UFrameEvent) {
            UFrameEvent e = (UFrameEvent) evt;
            var iec = IEC104_ScheduledTaskPool.getFromChannel(ctx);
            if (iec == null) return;
            if (!e.isStart() && e.isStart_con()) {
                iec.onReceiveStartDTCon();
                // 发送总召
                iec.sendInterrogationCommand();
            }
            if (!e.isTest() && e.isTest_con()) {
                log.info("收到 TESTFR_CON，链路正常");
                // 取消T1，重置T3
                iec.onReceiveTestFRCon();
            }
            if (!e.isStop() && e.isStop_con()) {
                // 停止确认
                ByteBuf result = IEC104_BasicInstructions.STOPDT_CON;
            }
            return;
        }
        ctx.fireUserEventTriggered(evt);
    }
}
