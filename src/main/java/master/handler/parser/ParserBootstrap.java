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
import java.util.ServiceLoader;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * ParserBootstrap类用于启动和管理Parser实例的缓存
 * 该类提供了一个线程安全、不可更改的Parser缓存映射
 */
public final class ParserBootstrap {

    /**
     * 线程安全、不可更改的缓存映射
     * 通过扫描ServiceLoader中所有带有ParserMeta注解的Parser实现类构建
     */
    public static final Map<Integer, Parser> CACHED = Map.copyOf(scan());

    /**
     * 扫描并加载所有Parser实现类，构建类型标识到Parser实例的映射
     * 只包含带有ParserMeta注解的Parser实现类
     *
     * @return 包含所有有效Parser实例的映射表
     * @throws IllegalStateException 当存在重复key时抛出异常
     */
    private static Map<Integer, Parser> scan() {

        // 获取 Parser 接口的所有实现类
        ServiceLoader<Parser> loader = ServiceLoader.load(Parser.class);

        return StreamSupport.stream(loader.spliterator(), false)
                // 如果反射获取的ParserMeta注解信息为null则过滤掉
                .filter(parser -> parser.getClass().getAnnotation(ParserMeta.class) != null)
                // 收集器
                .collect(Collectors.toMap(parser -> {
                    // 通过 getClass() 方法获取parser运行时类; 通过反射查找该类上可能存在的 ParserMeta 注解信息
                    ParserMeta meta = parser.getClass().getAnnotation(ParserMeta.class);
                    return key(meta.typeIdentifier(), meta.causeTx());
                }, Function.identity(), (a, b) -> {
                    throw new IllegalStateException("重复 key: " + a);
                }));
    }

    /**
     * 根据类型标识和传送原因生成唯一的键值
     * 通过位运算将两个参数组合成一个整数key
     *
     * @param typeIdentifier 类型标识字节
     * @param causeTx 传送原因短整型
     * @return 组合后的整数键值
     */
    public static int key(byte typeIdentifier, short causeTx) {
        // 只取类型标识的低 8 位和传送原因的低 16 位
        // 将类型标识左移 16 位与传送原因错开后进行 | 操作
        return (typeIdentifier & 0xFF) << 16 | (causeTx & 0xFFFF);
    }
}

