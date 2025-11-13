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
package master.handler.parser;

import io.netty.channel.ChannelHandlerContext;
import util.ByteBufResource;

/**
 * 解析器接口，定义了解析数据的方法规范
 */
public interface Parser {
    void parser(int ioa, ByteBufResource value, byte qualityDescriptors, ChannelHandlerContext ctx);
}

