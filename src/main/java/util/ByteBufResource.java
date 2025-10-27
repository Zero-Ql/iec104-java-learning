package util;

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

