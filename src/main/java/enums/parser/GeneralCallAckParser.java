package enums.parser;

import handler.Parser;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.log4j.Log4j2;
import util.ByteBufResource;

@Log4j2
public enum GeneralCallAckParser implements Parser {
    INSTANCE;

    @Override
    public void parser(int ioa, ByteBufResource value, byte qualityDescriptors, ChannelHandlerContext ctx) {
        log.info("总召确认(C_IC_NA_1 ActCon)    IOA={}    quality={}", ioa, qualityDescriptors);
    }
}
