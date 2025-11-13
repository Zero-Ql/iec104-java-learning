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
package enums.controlDirection;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 命令限定词枚举类
 * 用于遥控(单/双命令)与遥调(设定值)的限定词位操作
 * 对应 SCO / DCO / QOS 字节的位定义
 */
@RequiredArgsConstructor
@Getter
public enum CommandQualifier {

    SELECT(7),      // bit7: 0=执行，1=选择

    /** 单命令限定词：一位状态 */
    ON(6),          // bit6: 0=分/断，1=合/通

    /** 双命令限定词：两位状态 */
    DCO_STATE_0(6), // 两位状态，bit6~5
    DCO_STATE_1(5), // 00=不允许，01=分，10=合，11=不允许

    /** 遥调限定词：一位状态 */
    ACTIVATE(6);    // bit6: 0=不激活，1=激活（部分厂家扩展）

    private final int bitIndex;

    /** 读取某一位 */
    public static boolean isSet(byte qualifier, CommandQualifier cq) {
        return (qualifier & (1 << cq.bitIndex)) != 0;
    }

    /** 设置或清除某一位 */
    public static byte set(byte qualifier, CommandQualifier cq, boolean on) {
        return on ? (byte) (qualifier | (1 << cq.bitIndex))
                : (byte) (qualifier & ~(1 << cq.bitIndex));
    }

    /* ----------- 快捷工厂 ----------- */

    /** 生成单命令 SCO 字节 */
    public static byte buildSCO(boolean select, boolean on) {
        byte sco = 0;
        sco = set(sco, SELECT, select);
        sco = set(sco, ON, on);
        return sco;
    }

    /** 生成双命令 DCO 字节 */
    public static byte buildDCO(boolean select, int state /* 0=不允许,1=分,2=合 */) {
        if (state < 0 || state > 2) throw new IllegalArgumentException("state 只能 0/1/2");
        byte dco = 0;
        dco = set(dco, SELECT, select);
        dco |= (state & 0x03) << 5;   // 直接写两位
        return dco;
    }

    /** 生成设定值 QOS 字节（基础版，只支持 Select+Activate） */
    public static byte buildQOS(boolean select, boolean activate) {
        byte qos = 0;
        qos = set(qos, SELECT, select);
        qos = set(qos, ACTIVATE, activate);
        return qos;
    }
}