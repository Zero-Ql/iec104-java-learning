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
package impl.iec104.util;

import io.netty.buffer.ByteBuf;
import io.netty.util.ReferenceCountUtil;
/**
 * ByteBufResource是一个自动可关闭的ByteBuf包装类，用于管理ByteBuf的生命周期。
 * 该类实现了AutoCloseable接口，确保在使用完毕后能够正确释放ByteBuf资源。
 */
public record ByteBufResource(ByteBuf byteBuf) implements AutoCloseable {
    /**
     * 创建一个新的ByteBufResource实例的静态工厂方法。
     *
     * @param byteBuf 要包装的ByteBuf对象，不能为null
     * @return 新创建的ByteBufResource实例
     */
    public static ByteBufResource of(ByteBuf byteBuf) {
        return new ByteBufResource(byteBuf);
    }

    /**
     * 关闭并释放ByteBuf资源。
     * 该方法会调用ReferenceCountUtil.release来正确释放ByteBuf的引用计数。
     */
    @Override
    public void close() {
        ReferenceCountUtil.release(byteBuf);
    }
}

