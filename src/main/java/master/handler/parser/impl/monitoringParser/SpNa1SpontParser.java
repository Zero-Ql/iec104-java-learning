package master.handler.parser.impl.monitoringParser;

import com.google.auto.service.AutoService;
import enums.monitoringDirections.QualityBit;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.log4j.Log4j2;
import master.handler.parser.Parser;
import master.handler.parser.ParserMeta;
import util.ByteBufResource;


/**
 * SpNa1SpontParser类用于解析突变遥信
 */
@Log4j2
@AutoService(Parser.class)
@ParserMeta(typeIdentifier = 0x01, causeTx = 0x03)
public class SpNa1SpontParser implements Parser{
    /**
     * 解析输入的数据并记录相关信息到日志中。
     *
     * @param ioa                信息对象地址
     * @param value              包含待处理数据的字节缓冲区资源
     * @param qualityDescriptors 质量描述符字节，用于表示数据的各种状态位
     * @param ctx                通道处理器上下文，提供网络通信相关功能
     */
    @Override
    public void parser(int ioa, ByteBufResource value, byte qualityDescriptors, ChannelHandlerContext ctx) {
        // 使用try-with-resources确保valueResource在使用完毕后被正确关闭
        try (ByteBufResource valueResource = value) {
            // 记录总召响应的相关信息，包括IOA和各种质量位的状态
            log.info("Yx 突变  IOA：{}  IV：{}  NT：{}  SB：{}  BL：{}  SPI：{}", ioa,
                    QualityBit.isSet(qualityDescriptors, QualityBit.INVALID),
                    QualityBit.isSet(qualityDescriptors, QualityBit.NOT_CURRENT),
                    QualityBit.isSet(qualityDescriptors, QualityBit.SUBSTITUTED),
                    QualityBit.isSet(qualityDescriptors, QualityBit.BLOCKED),
                    QualityBit.isSet(qualityDescriptors, QualityBit.OVERFLOW) ? "On" : "Off");
        }
    }
}
