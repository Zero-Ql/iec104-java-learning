package handler;

import io.netty.channel.ChannelHandlerContext;
import util.ByteBufResource;

public interface Parser {
    void parser(int ioa, ByteBufResource value, byte qualityDescriptors, ChannelHandlerContext ctx);
}
