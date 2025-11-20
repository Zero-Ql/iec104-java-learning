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
package impl.iec104.frame;


import impl.iec104.frame.apci.IEC104_ApciMessageDetail;
import impl.iec104.frame.asdu.IEC104_AsduMessageDetail;
import lombok.Data;

/**
 * 拼接完整帧
 */
@Data
public class IEC104_FrameBuilder {
    private final IEC104_ApciMessageDetail apciMessageDetail;
    private IEC104_AsduMessageDetail asduMessageDetail;

    private IEC104_FrameBuilder(Builder builder) {
        this.apciMessageDetail = builder.apciMessageDetail;
        this.asduMessageDetail = builder.asduMessageDetail;
    }

    public static class Builder {
        private final IEC104_ApciMessageDetail apciMessageDetail;
        private IEC104_AsduMessageDetail asduMessageDetail = null;

        public Builder(IEC104_ApciMessageDetail apciMessageDetail) {
            this.apciMessageDetail = apciMessageDetail;
        }

        public Builder setAsduMessageDetail(IEC104_AsduMessageDetail asduMessageDetail) {
            this.asduMessageDetail = asduMessageDetail;
            return this;
        }

        public IEC104_FrameBuilder build() {
            return new IEC104_FrameBuilder(this);
        }
    }
}