package master.handler.parser;

import io.netty.channel.ChannelHandlerContext;
import util.ByteBufResource;

/**
 * 解析器接口，定义了解析数据的方法规范
 */
public interface Parser {
    void parser(int ioa, ByteBufResource value, byte qualityDescriptors, ChannelHandlerContext ctx);
}

