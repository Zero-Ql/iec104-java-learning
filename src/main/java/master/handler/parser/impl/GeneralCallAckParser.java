package master.handler.parser.impl;

import com.google.auto.service.AutoService;
import frame.IEC104_MessageInfo;
import io.netty.buffer.ByteBuf;
import master.handler.parser.Parser;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.log4j.Log4j2;
import master.handler.parser.ParserMeta;
import util.ByteBufResource;

import java.util.List;

/**
 * GeneralCallAckParser类用于解析通用调用确认消息
 * 该类实现了Parser接口，专门处理类型标识符为0x64，传输原因码为0x07的通用调用确认帧
 * <p><b>所有权转移：</b>调用者不再持有资源，本方法负责关闭。</p>
 */
@Log4j2
@AutoService(Parser.class)
@ParserMeta(typeIdentifier = 0x64, causeTx = 0x07)
public class GeneralCallAckParser implements Parser {

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
        try (ByteBufResource valueResource = value) {
            // 记录通用调用确认的IOA和质量描述符信息
            log.info("GeneralCallAck  IOA：{}  QualityDescriptors：{}", ioa, qualityDescriptors);
        }
    }
}

