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

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor
public enum IEC104_TypeIdentifier {

    /**
     * 单点遥信
     */
    M_SP_NA_1((byte) 0x01, 0, true),

    /**
     * 带时标的单点遥信
     */
    M_SP_TB_1((byte) 0x1E, 1, true),

    /**
     * 双点遥信
     */
    M_DP_NA_1((byte) 0x03, 0, true),

    /**
     * 带时标的双点遥信
     */
    M_DP_TB_1((byte) 0x1F, 1, true),

    /**
     * 遥测，归一化值
     */
    M_ME_NA_1((byte) 0x09, 2, true),

    /**
     * 带时标的遥测，归一化值
     */
    M_ME_TD_1((byte) 0x22, 2, true),

    /**
     * 遥测，标度化值
     */
    M_ME_NB_1((byte) 0x0B, 2, true),

    /**
     * 带时标的遥测，标度化值
     */
    M_ME_TE_1((byte) 0x35, 2, true),

    /**
     * 遥测，短浮点数
     */
    M_ME_NC_1((byte) 0x0D, 4, true),

    /**
     * 带时标的遥测，短浮点数
     */
    M_ME_TF_1((byte) 0x36, 4, true),

    /**
     * 单命令，遥控
     */
    C_SC_NA_1((byte) 0x2D, 1, false),

    /**
     * 带时标的遥控，单命令
     */
    C_SC_TA_1((byte) 0x3A, 1, false),

    /**
     * 双命令，遥控
     */
    C_DC_NA_1((byte) 0x2E, 1, false),

    /**
     * 带时标的遥控，双命令
     */
    C_DC_TA_1((byte) 0x3B, 1, false),

    /**
     * 遥调，归一化值
     */
    C_SE_NA_1((byte) 0x30, 2, false),

    /**
     * 带时标的遥调，归一化值
     */
    C_SE_TA_1((byte) 0x3D, 2, false),

    /**
     * 遥调，标度化值
     */
    C_SE_NB_1((byte) 0x31, 2, false),

    /**
     * 带时标的遥调，标度化值
     */
    C_SE_TB_1((byte) 0x3E, 2, false),

    /**
     * 遥调，短浮点数
     */
    C_SE_NC_1((byte) 0x32, 2, false),

    /**
     * 带时标的遥调，短浮点数
     */
    C_SE_TC_1((byte) 0x3F, 2, false),

    /**
     * 总召唤
     */
    C_IC_NA_1((byte) 0x64, 0, true),

    /**
     * 时钟同步命令
     */
    C_CS_NA_1((byte) 0x67, 7, false),

    /**
     * 复位进程命令
     */
    C_RP_NA_1((byte) 0x69, 1, false);

    /**
     * 类型标识
     */
    private final byte value;
    /**
     * NVA 长度
     */
    private final int msgLen;

    /**
     * 是否带有品质标识符
     */
    private final boolean hasQuality;

    private static final Map<Byte, IEC104_TypeIdentifier> VALUE_MAP = Stream.of(
            values()).collect(Collectors.toMap(IEC104_TypeIdentifier::getValue, Function.identity()));

    public static Optional<IEC104_TypeIdentifier> getIEC104TypeIdentifier(byte value) {
        return Optional.ofNullable(VALUE_MAP.get(value));
    }
}