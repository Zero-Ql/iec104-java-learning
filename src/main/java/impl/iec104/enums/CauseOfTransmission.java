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
package impl.iec104.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor
public enum CauseOfTransmission {


    /**
     * 周期/循环
     */
    PER_CYC((short) 0x01),

    /**
     * 背景扫描
     */
    BACK((short) 0x02),

    /**
     * 突变
     */
    SPONT((short) 0x03),

    /**
     * 被请求
     */
    REQ((short) 0x05),



    // 总召传送原因
    /**
     * 激活
     */
    ACT((short) 0x06),
    /**
     * 激活确认
     */
    ACT_CON((short) 0x07),
    /**
     * 停止激活
     */
    DE_ACT((short) 0x08),
    /**
     * 停止激活确认
     */
    DE_ACT_CON((short) 0x09),
    /**
     * 激活终止
     */
    ACT_TERM((short) 0x0A),


    // 公共传送原因
    /**
     * 响应总召唤
     */
    INTROGEN((short) 0x14);

    /**
     * 传输原因标识符 (Cause of Transmission)
     */
    private final short cot;

    private static final Map<Short, CauseOfTransmission> INDEX = Stream.of(
            values()).collect(
            Collectors.toMap(CauseOfTransmission::getCot, Function.identity()));

    public static Optional<CauseOfTransmission> of(short cot) {
        return Optional.ofNullable(INDEX.get(cot));
    }
}
