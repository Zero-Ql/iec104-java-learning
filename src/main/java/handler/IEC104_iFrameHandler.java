package handler;

import core.control.IEC104_controlField;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.log4j.Log4j2;

@Log4j2
public abstract class IEC104_iFrameHandler extends ChannelHandlerAdapter {

    /**
     * 处理通道读取事件
     * <p>
     * 该方法用于解析接收到的数据，判断是否为I帧，如果是则进行相应处理，否则将数据传递给下一个处理器
     *
     * @param ctx 通道处理上下文
     * @param msg 接收到的消息对象
     * @throws Exception 处理过程中可能抛出的异常
     */
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // TODO 考虑解析seq序号（发送、接收）
        var result = (ByteBuf) msg;
        // 存档
        result.markReaderIndex();
        var bytes = new byte[6];

        // 读取头、长度、控制域
        result.readBytes(bytes);

        // 判断是否为 I 帧
        if (isIFrame(bytes)) {
            var iFrameLen = IEC104_checkTheDataHandler.getFrameLength(bytes, 4);

            ByteBufAllocator allocator = ctx.alloc();

            var data = allocator.buffer(iFrameLen);
            result.readBytes(data);

            if (data != null) {
                // 由子类实现
                iInstructionHandler(ctx, data);
                // 已处理该帧，不再向下传递，直接返回
                return;
            }
        }
        // 非 I帧回滚后原样传给下个处理器
        result.resetReaderIndex();
        // 继续让后续处理器处理
        ctx.fireChannelRead(result);
    }

    private boolean isIFrame(byte[] bytes) {
        // 长度域必须为 4
        if (IEC104_checkTheDataHandler.getFrameLength(bytes, 4) != 0x4) return false;
        // 指定为 IEC104帧
        if (IEC104_checkTheDataHandler.isFrameStart(bytes[0])) return false;
        // 注：wireshark 的控制域字节从右向左
        // 控制域第0bit为 0, 1bit 为 1
        return IEC104_controlField.isTypeI(bytes);
    }

    public abstract void iInstructionHandler(ChannelHandlerContext ctx, ByteBuf payload);

}
