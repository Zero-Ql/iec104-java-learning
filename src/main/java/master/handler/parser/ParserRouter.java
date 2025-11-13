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

import java.util.Map;

/**
 * ParserRouter类用于根据类型标识符和原因码查找对应的解析器
 * 这是一个单例模式的实现，提供全局唯一的解析器路由功能
 */
public final class ParserRouter {
    private static final ParserRouter INSTANCE = new ParserRouter();
    private static final Map<Integer, Parser> table = ParserBootstrap.CACHED;

    /**
     * 获取ParserRouter的单例实例
     * @return 返回ParserRouter的唯一实例
     */
    public static ParserRouter getInstance() {
        return INSTANCE;
    }

    /**
     * 根据类型标识符和原因码查找对应的解析器
     * @param typeIdentifier 类型标识符，用于区分不同的数据类型
     * @param causeTx 原因码，用于进一步细化解析器的选择
     * @return 返回对应的解析器实例，如果未找到则返回null
     */
    public Parser lookup(byte typeIdentifier, short causeTx) {
        // 使用ParserBootstrap生成的键值从缓存表中查找解析器
        return table.get(ParserBootstrap.key(typeIdentifier, causeTx));
    }
}

