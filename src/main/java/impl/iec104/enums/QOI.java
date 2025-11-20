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
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * QOI（Qualifier Of Interrogation）限定词枚举
 * 用于 C_IC_NA_1 报文，决定“扫哪些组”
 * 符合 IEC 60870-5-101 Table 11
 */
@RequiredArgsConstructor
@Getter
public enum QOI {

    GLOBAL_STATION((byte) 0x14, "总召唤（全局）"),
    GROUP_1((byte) 0x15, "组 1 召唤"),
    GROUP_2((byte) 0x16, "组 2 召唤"),
    GROUP_3((byte) 0x17, "组 3 召唤"),
    GROUP_4((byte) 0x18, "组 4 召唤"),
    GROUP_5((byte) 0x19, "组 5 召唤"),
    GROUP_6((byte) 0x1A, "组 6 召唤"),
    GROUP_7((byte) 0x1B, "组 7 召唤"),
    GROUP_8((byte) 0x1C, "组 8 召唤"),
    GROUP_9((byte) 0x1D, "组 9 召唤"),
    GROUP_10((byte) 0x1E, "组 10 召唤"),
    GROUP_11((byte) 0x1F, "组 11 召唤"),
    GROUP_12((byte) 0x20, "组 12 召唤"),
    GROUP_13((byte) 0x21, "组 13 召唤"),
    GROUP_14((byte) 0x22, "组 14 召唤"),
    GROUP_15((byte) 0x23, "组 15 召唤"),
    GROUP_16((byte) 0x24, "组 16 召唤"),

    RESERVED_COMPAT_1((byte) 0x01, "保留（兼容）"),
    RESERVED_COMPAT_19((byte) 0x13, "保留（兼容）"),
    RESERVED_FUTURE_37((byte) 0x25, "保留（37-63）"),
    RESERVED_VENDOR((byte) 0x40, "厂商自定义起点"),

    UNKNOWN((byte) 0x00, "未知/未映射");

    private final byte value;
    private final String desc;

    /* ---------- 快速索引 ---------- */
    private static final Map<Byte, QOI> MAP = Stream.of(values())
            .collect(Collectors.toMap(QOI::getValue, Function.identity()));

    public static QOI of(byte value) {
        return MAP.getOrDefault(value, UNKNOWN);
    }

    /** 是否总召唤 */
    public boolean isGlobal() {
        return this == GLOBAL_STATION;
    }

    /** 是否组召唤（1-16） */
    public boolean isGroup() {
        return value >= GROUP_1.getValue() && value <= GROUP_16.getValue();
    }

    /** 获取组号 1-16，非组返回 -1 */
    public int groupNo() {
        return isGroup() ? (value - GROUP_1.getValue() + 1) : -1;
    }

    /** 友好字符串 */
    @Override
    public String toString() {
        return String.format("0x%02X-%s", value & 0xFF, desc);
    }
}