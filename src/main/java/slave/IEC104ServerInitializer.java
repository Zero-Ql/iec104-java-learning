package slave;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import master.IEC104Decoder;
import master.IEC104Encoder;
import slave.handler.uFrameSlaveHandler;

public class IEC104ServerInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        socketChannel.pipeline().addLast(new LengthFieldBasedFrameDecoder(65535, 0, 2, 0, 2));
        socketChannel.pipeline().addLast(new LengthFieldPrepender(2));

        socketChannel.pipeline().addLast(new IEC104Decoder());
        socketChannel.pipeline().addLast(new IEC104Encoder());

        socketChannel.pipeline().addLast(new uFrameSlaveHandler());

        socketChannel.pipeline().addLast(new IEC104ServerHandler());
    }
}
