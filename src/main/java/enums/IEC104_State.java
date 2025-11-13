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
package enums;

public enum IEC104_State {
    /**
     * 断开
     */
    DISCONNECTED,
    /**
     * 正在连接
     */
    CONNECTING,
    /**
     * 等待启动链路帧确认
     */
    WAIT_STARTED_CON,
    /**
     * 链路已建立
     */
    LINK_ESTABLISHED,
    /**
     * 正在总召
     */
    GENERAL_CALL,
    /**
     * 数据交互中
     */
    DATA_EXCHANGE,
    /**
     * 仅维持心跳
     */
    HEARTBEAT_ONLY,
    /**
     * 正在重连
     */
    RECONNECTING
}
