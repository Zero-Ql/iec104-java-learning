package master.handler.parser.impl.monitoringParser;

import com.google.auto.service.AutoService;
import enums.monitoringDirections.QualityBit;
import master.handler.parser.Parser;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.log4j.Log4j2;
import master.handler.parser.ParserMeta;
import util.ByteBufResource;

import java.nio.ByteOrder;

/**
 * MeNc1SpontParser类用于解析遥测数据 spontaneous 报文
 * 该解析器处理类型标识为0x0D，传输原因标识为0x03的数据
 * <p><b>所有权转移：</b>调用者不再持有资源，本方法负责关闭。</p>
 */
@Log4j2
@AutoService(Parser.class)
@ParserMeta(typeIdentifier = 0x0D, causeTx = 0x03)
public class MeNc1SpontParser implements Parser {

    /**
     * 解析遥测数据 spontaneous 报文的具体实现方法
     *
     * @param ioa                信息对象地址
     * @param value              数据值缓冲区资源
     * @param qualityDescriptors 质量描述符字节
     * @param ctx                通道处理器上下文
     */
    @Override
    public void parser(int ioa, ByteBufResource value, byte qualityDescriptors, ChannelHandlerContext ctx) {
        try (ByteBufResource valueResource = value) {
            // 从缓冲区中读取浮点数值
            float v = valueResource.byteBuf().order(ByteOrder.LITTLE_ENDIAN).readFloat();
            // 记录遥测数据解析日志
            log.info("YC 突变  IOA：{}  Value：{}  IV：{}  NT：{}  SB：{}  BL：{}  OV：{}", ioa, v,
                    QualityBit.isSet(qualityDescriptors, QualityBit.INVALID),
                    QualityBit.isSet(qualityDescriptors, QualityBit.NOT_CURRENT),
                    QualityBit.isSet(qualityDescriptors, QualityBit.SUBSTITUTED),
                    QualityBit.isSet(qualityDescriptors, QualityBit.BLOCKED),
                    QualityBit.isSet(qualityDescriptors, QualityBit.OVERFLOW));
        }
    }
}

