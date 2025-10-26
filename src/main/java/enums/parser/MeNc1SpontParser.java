package enums.parser;

import handler.Parser;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.log4j.Log4j2;
import util.ByteBufResource;

@Log4j2
public enum MeNc1SpontParser implements Parser {
    INSTANCE;

    @Override
    public void parser(int ioa, ByteBufResource value, byte qualityDescriptors, ChannelHandlerContext ctx) {
        float v = value.getByteBuf().readFloat();
        log.info("YC 突变响应: ioa={}, value={}, qualityDescriptors={}", ioa, v, qualityDescriptors);
    }
}
