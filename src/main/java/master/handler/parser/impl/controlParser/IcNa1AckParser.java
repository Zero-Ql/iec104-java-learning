package master.handler.parser.impl.controlParser;

import com.google.auto.service.AutoService;
import enums.QOI;
import master.handler.parser.Parser;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.log4j.Log4j2;
import master.handler.parser.ParserMeta;
import util.ByteBufResource;

/**
 * GeneralCallAckParser类用于解析通用调用确认消息
 * 该类实现了Parser接口，专门处理类型标识符为0x64，传输原因码为0x07的通用调用确认帧
 * <p><b>所有权转移：</b>调用者不再持有资源，本方法负责关闭。</p>
 */
@Log4j2
@AutoService(Parser.class)
@ParserMeta(typeIdentifier = 0x64, causeTx = 0x07)
public class IcNa1AckParser implements Parser {

    /**
     * 解析通用调用确认消息
     *
     * @param ioa                信息对象地址，表示被确认的通用调用的信息对象地址
     * @param value              包含确认数据的字节缓冲区资源
     * @param qualityDescriptors 质量描述符，表示确认状态的质量信息
     * @param ctx                通道处理器上下文，用于网络通信操作
     */
    @Override
    public void parser(int ioa, ByteBufResource value, byte qualityDescriptors, ChannelHandlerContext ctx) {
        // 创建一个try-with-resources块，用于自动释放valueResource
        try (ByteBufResource valueResource = value) {
            // 记录总召确认的IOA和质量描述符信息
            QOI qoi = QOI.of(qualityDescriptors);
            if (qoi.isGlobal()) {
                log.info("确认  IOA：{}  {}", ioa, qoi.toString());
            } else if (qoi.isGroup()) {
                log.info(qoi.toString());
            }
        }
    }
}

