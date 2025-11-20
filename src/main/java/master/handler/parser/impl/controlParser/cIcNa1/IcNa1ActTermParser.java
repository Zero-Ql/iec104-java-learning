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
package master.handler.parser.impl.controlParser.cIcNa1;

import com.google.auto.service.AutoService;
import impl.iec104.enums.QOI;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.log4j.Log4j2;
import master.handler.parser.Parser;
import master.handler.parser.ParserMeta;
import impl.iec104.util.ByteBufResource;

/**
 * IcNa1ActTermParser类用于解析总召终止
 */
@Log4j2
@AutoService(Parser.class)
@ParserMeta(typeIdentifier = 0x64, causeTx = 0x0A)
public class IcNa1ActTermParser implements Parser {

    /**
     * 解析总召终止报文
     * @param ioa 信息对象地址
     * @param value 报文数据缓冲区资源
     * @param qualityDescriptors 质量描述符字节
     * @param ctx 通道处理上下文
     */
    @Override
    public void parser(int ioa, ByteBufResource value, byte qualityDescriptors, ChannelHandlerContext ctx) {
        try (ByteBufResource valueResource = value) {
            // 记录总召确认的IOA和质量描述符信息
            QOI qoi = QOI.of(qualityDescriptors);
            if (qoi.isGlobal()) {
                log.info("结束  IOA：{}  {}", ioa, qoi.toString());
            } else if (qoi.isGroup()) {
                log.info(qoi.toString());
            }
        }
    }
}

