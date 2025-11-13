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
package master.handler;

import core.scheduler.IEC104_ScheduledTaskPool;
import handler.IEC104_seqManager;
import io.netty.channel.ChannelHandlerContext;

public class MasterSeqManager extends IEC104_seqManager {
    /**
     * 自定义任务
     * ChannelHandlerContext ctx 通道上下文
     * AtomicInteger recvOrdinal 本地接收序号的原子引用
     */
    @Override
    public void customTasks(ChannelHandlerContext ctx, int recvOrdinal) {
        // 发送 S 帧
        IEC104_ScheduledTaskPool.getFromChannel(ctx).sendSFrame((short) super.recvOrdinal.get());

    }
}
