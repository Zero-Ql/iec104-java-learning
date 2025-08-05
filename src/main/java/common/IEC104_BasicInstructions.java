package common;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class IEC104_BasicInstructions {
    /**
     * 定义启动数据传输的请求帧
     */
    public static final ByteBuf STARTDT_ACT = Unpooled.unmodifiableBuffer(Unpooled.wrappedBuffer(new byte[]{0x68, 0x04, 0x07, 0x00, 0x00, 0x00}));

    /**
     * 定义启动数据传输的确认帧
     */
    public static final ByteBuf STARTDT_CON = Unpooled.unmodifiableBuffer(Unpooled.wrappedBuffer(new byte[]{0x68, 0x04, 0xB, 0x00, 0x00, 0x00}));

    /**
     * 定义停止数据传输的请求帧
     */
    public static final ByteBuf STOPDT_ACT = Unpooled.unmodifiableBuffer(Unpooled.wrappedBuffer(new byte[]{0x68, 0x04, 0x13, 0x00, 0x00, 0x00}));

    /**
     * 定义停止数据传输的确认帧
     */
    public static final ByteBuf STOPDT_CON = Unpooled.unmodifiableBuffer(Unpooled.wrappedBuffer(new byte[]{0x68, 0x04, 0x23, 0x00, 0x00, 0x00}));

    /**
     * 定义测试帧的请求帧
     */
    public static final ByteBuf TESTFR_ACT = Unpooled.unmodifiableBuffer(Unpooled.wrappedBuffer(new byte[]{0x68, 0x04, 0x43, 0x00, 0x00, 0x00}));

    /**
     * 定义测试帧的确认帧
     */
    public static final ByteBuf TESTFR_CON = Unpooled.unmodifiableBuffer(Unpooled.wrappedBuffer(new byte[]{0x68, 0x04, (byte) 0x83, 0x00, 0x00, 0x00}));

    private IEC104_BasicInstructions() {
    }
}
