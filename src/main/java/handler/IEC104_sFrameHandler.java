//package handler;
//
//import io.netty.buffer.ByteBuf;
//import io.netty.channel.ChannelHandlerAdapter;
//import io.netty.channel.ChannelHandlerContext;
//
//public class IEC104_sFrameHandler extends ChannelHandlerAdapter {
//    @Override
//    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        var result = (ByteBuf)msg;
//        result.markReaderIndex();
//
//    }
//}
