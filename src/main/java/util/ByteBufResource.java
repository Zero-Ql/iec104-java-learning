package util;

import io.netty.buffer.ByteBuf;
import io.netty.util.ReferenceCountUtil;

public class ByteBufResource implements AutoCloseable {
    private final ByteBuf byteBuf;

    public ByteBufResource(ByteBuf byteBuf) {
        this.byteBuf = byteBuf;
    }

    public static ByteBufResource of(ByteBuf byteBuf) {
        return new ByteBufResource(byteBuf);
    }

    public ByteBuf getByteBuf() {
        return byteBuf;
    }

    @Override
    public void close() {
        ReferenceCountUtil.release(byteBuf);
    }
}
