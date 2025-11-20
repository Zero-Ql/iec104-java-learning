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
package impl.iec104.frame.apci;

import lombok.Data;

@Data
public class IEC104_ApciMessageDetail {

    /**
     * APDU 长度
     */
//    private int apduLen = 0;

    /**
     * 控制域 固定 四字节
     * 发送 两字节
     */
    private short sendOrdinal;

    /**
     * 接收 两字节
     */
    private short recvOrdinal;

    public IEC104_ApciMessageDetail(short sendOrdinal, short recvOrdinal){
        this.sendOrdinal = sendOrdinal;
        this.recvOrdinal = recvOrdinal;
    }
}