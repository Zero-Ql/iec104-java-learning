package master.handler;

import enums.CauseOfTransmission;
import enums.IEC104_TypeIdentifier;
import frame.IEC104_MessageInfo;
import frame.asdu.IEC104_AsduMessageDetail;
import frame.asdu.IEC104_VSQ_COT_OA;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.log4j.Log4j2;
import master.handler.parser.ParserRouter;
import util.ByteBufResource;

import java.util.List;
import java.util.NoSuchElementException;

@Log4j2
public class IEC104_iFrameMasterHandler extends SimpleChannelInboundHandler<IEC104_AsduMessageDetail> {

    /**
     * @param ctx  通道上下文
     * @param asdu asdu对象
     */
    @Override
    protected void messageReceived(ChannelHandlerContext ctx, IEC104_AsduMessageDetail asdu) {
        if (asdu instanceof IEC104_AsduMessageDetail payload) {
            byte typeIdentifier = payload.getTypeIdentifier();
            // 通过构建器创建 IEC104_VSQ_COT_OA 对象
            IEC104_VSQ_COT_OA vsqCotOa = new IEC104_VSQ_COT_OA.Builder(
                    payload.getVariableStructureQualifiers(),
                    payload.getTransferReason(),
                    payload.getSenderAddress()
            ).build();
            boolean sq = vsqCotOa.isSQ();
            short numIx = vsqCotOa.getNumIx();
            boolean test = vsqCotOa.isTest();
            boolean negative = vsqCotOa.isNegative();
            short causeTx = vsqCotOa.getCauseTx();
            byte senderAddress = vsqCotOa.getSenderAddress();
            short publicAddress = payload.getPublicAddress();
            List<IEC104_MessageInfo> IOA = payload.getIOA();
            if (IOA == null) {
                log.warn("IOA列表为空");
                return;
            }
            log.info("接收到I帧：{}", payload);
            String headerLog = String.format(
                    """
                            类型标识：%s
                            可变结构限定词(SQ)：%b
                            可变结构限定词(NumIx)：%d
                            传送原因(Test)：%b
                            传送原因(Negative)：%b
                            传送原因(CauseTx)：%d
                            发送方地址(OA)：%d
                            公共地址(Addr)：%d""",
                    typeIdentifier, sq, numIx, test, negative, causeTx, senderAddress, publicAddress
            );
            log.info(headerLog);

            // 如果无法解析传送原因则抛出异常
            short cot = CauseOfTransmission.of(causeTx)
                    .orElseThrow(() -> new NoSuchElementException("无法解析的传送原因：" + causeTx))
                    .getCot();

            // 获取接口路由实例
            ParserRouter parserRouter = ParserRouter.getInstance();

            try {
                // 通过类型标识和传送原因组合为一个唯一键，这个键对应一个唯一的IOA结构
                // 通过键获取对应的解析器
                IOA.forEach(info -> parserRouter.lookup(typeIdentifier, cot).parser(info.getMessageAddress(), ByteBufResource.of(info.getValue()), info.getQualityDescriptors(), ctx));
            } catch (NullPointerException e) {
                log.error("无法解析的I帧(未找到对应解析器)：{}", payload);
            }
        }
    }
}
