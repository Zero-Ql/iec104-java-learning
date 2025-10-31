package master.handler.parser.impl.controlParser;

import com.google.auto.service.AutoService;
import enums.monitoringDirections.QualityBit;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.log4j.Log4j2;
import master.handler.parser.Parser;
import master.handler.parser.ParserMeta;
import util.ByteBufResource;

import java.nio.ByteOrder;

@Log4j2
@AutoService(Parser.class)
@ParserMeta(typeIdentifier = 0x0D, causeTx = 0x14)
public class IcNa1IntrogenParser implements Parser {
    @Override
    public void parser(int ioa, ByteBufResource value, byte qualityDescriptors, ChannelHandlerContext ctx) {
        // 创建一个try-with-resources块，用于自动释放valueResource
        try (ByteBufResource valueResource = value) {
            // 记录总召响应的IOA、value和质量描述符信息
            float v = valueResource.byteBuf().order(ByteOrder.LITTLE_ENDIAN).readFloat();
            log.info("总召响应  IOA：{}  Value：{}  IV：{}  NT：{}  SB：{}  BL：{}  OV：{}", ioa, v,
                    QualityBit.isSet(qualityDescriptors, QualityBit.INVALID),
                    QualityBit.isSet(qualityDescriptors, QualityBit.NOT_CURRENT),
                    QualityBit.isSet(qualityDescriptors, QualityBit.SUBSTITUTED),
                    QualityBit.isSet(qualityDescriptors, QualityBit.BLOCKED),
                    QualityBit.isSet(qualityDescriptors, QualityBit.OVERFLOW));
        }
    }
}
