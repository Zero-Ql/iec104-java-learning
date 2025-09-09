package slave;

import core.codec.IEC104_Decoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import core.codec.IEC104_Encoder;
import slave.handler.IEC104_uFrameSlaveHandler;

public class IEC104_ServerInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        socketChannel.pipeline().addLast(new LengthFieldBasedFrameDecoder(65535, 0, 2, 0, 2));
        socketChannel.pipeline().addLast(new LengthFieldPrepender(2));

        socketChannel.pipeline().addLast(new IEC104_Decoder());
        socketChannel.pipeline().addLast(new IEC104_Encoder());

        socketChannel.pipeline().addLast(new IEC104_uFrameSlaveHandler());

        socketChannel.pipeline().addLast(new IEC104_ServerHandler());
    }
}
