package master.handler.parser.impl.controlParser;

import com.google.auto.service.AutoService;
import enums.QOI;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.log4j.Log4j2;
import master.handler.parser.Parser;
import master.handler.parser.ParserMeta;
import util.ByteBufResource;

@Log4j2
@AutoService(Parser.class)
@ParserMeta(typeIdentifier = 0x64, causeTx = 0x0A)
public class IcNa1ActTermParser implements Parser {
    @Override
    public void parser(int ioa, ByteBufResource value, byte qualityDescriptors, ChannelHandlerContext ctx) {
        try (ByteBufResource valueResource = value) {
            // 记录总召确认的IOA和质量描述符信息
            QOI qoi = QOI.of(qualityDescriptors);
            if (qoi.isGlobal()) {
                log.info("结束  IOA：{}  {}", ioa, qoi.toString());
            } else if (qoi.isGroup()) {
                log.info(qoi.toString());
            }
        }
    }
}
