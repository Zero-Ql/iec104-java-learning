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
package frame.asdu;

import lombok.Data;

@Data
public class IEC104_VSQ_COT_OA {
    private final boolean SQ;
    private final short NumIx;
    private final boolean Test;
    private final boolean Negative;
    private final short CauseTx;
    private final byte SenderAddress;

    private IEC104_VSQ_COT_OA(Builder builder) {
        this.SQ = builder.SQ;
        this.NumIx = builder.NumIx;
        this.Test = builder.Test;
        this.Negative = builder.Negative;
        this.CauseTx = builder.CauseTx;
        this.SenderAddress = builder.SenderAddress;
    }

    public static class Builder {
        private final boolean SQ;
        private final short NumIx;
        private final boolean Test;
        private final boolean Negative;
        private final short CauseTx;
        private final byte SenderAddress;

        public Builder(byte VSQ, byte COT, byte OA) {
            // 提取第7位
            this.SQ = (VSQ & 0x80) != 0;
            // 提取第6-0位
            this.NumIx = (short) (VSQ & 0x7F);
            // 提取第7位
            this.Test = (COT & 0x80) != 0;
            // 提取第6位
            this.Negative = (COT & 0x40) != 0;
            // 提取第5-0位
            this.CauseTx = (short) (COT & 0x3F);
            this.SenderAddress = OA;
        }

        public IEC104_VSQ_COT_OA build() {
            return new IEC104_VSQ_COT_OA(this);
        }
    }
}
