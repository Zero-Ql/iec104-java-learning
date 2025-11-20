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
package slave.handler;

import impl.iec104.handler.IEC104_seqManager;
import io.netty.channel.ChannelHandlerContext;

public class SlaveSeqManager extends IEC104_seqManager {
    /**
     * 更新本地接收序号
     *
     * @param ctx         通道上下文
     * @param recvOrdinal 接收序号的原子引用
     */
    @Override
    public void customTasks(ChannelHandlerContext ctx, int recvOrdinal) {
        // 如果接收序号大于本地确认序号，则更新(用于子站确认主站的接收数)
        if (recvOrdinal > super.lastAck.get()) super.lastAck.set((short) recvOrdinal);
    }
}
