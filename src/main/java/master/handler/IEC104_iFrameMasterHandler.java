package master.handler;

import frame.IEC104_MessageInfo;
import frame.asdu.IEC104_AsduMessageDetail;
import frame.asdu.IEC104_VSQ_COT_OA;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.log4j.Log4j2;

import java.util.List;
import java.util.stream.IntStream;

@Log4j2
public class IEC104_iFrameMasterHandler extends SimpleChannelInboundHandler<IEC104_AsduMessageDetail> {

    /**
     * @param ctx  通道上下文
     * @param asdu asdu对象
     */
    @Override
    protected void messageReceived(ChannelHandlerContext ctx, IEC104_AsduMessageDetail asdu) {
        if (asdu instanceof IEC104_AsduMessageDetail) {
            byte typeIdentifier = asdu.getTypeIdentifier();
            IEC104_VSQ_COT_OA vsqCotOa = new IEC104_VSQ_COT_OA.Builder(asdu.getVariableStructureQualifiers(), asdu.getTransferReason(), asdu.getSenderAddress()).build();
            boolean sq = vsqCotOa.isSQ();
            short numIx = vsqCotOa.getNumIx();
            boolean test = vsqCotOa.isTest();
            boolean negative = vsqCotOa.isNegative();
            short causeTx = vsqCotOa.getCauseTx();
            byte senderAddress = vsqCotOa.getSenderAddress();
            short publicAddress = asdu.getPublicAddress();
            List<IEC104_MessageInfo> IOA = asdu.getIOA();
            log.info("接收到I帧：%s".formatted(asdu));
            log.info("""
                    类型标识：%s
                    可变结构限定词(SQ)：%b
                    可变结构限定词(NumIx)：%d
                    传送原因(Test)：%b
                    传送原因(Negative)：%b
                    传送原因(CauseTx)：%d
                    发送方地址(OA)：%d
                    公共地址(Addr)：%d
                    """.formatted(typeIdentifier, sq, numIx, test, negative, causeTx, senderAddress, publicAddress));

            IntStream.range(0, IOA.size())
                    .forEach(i -> {
                        IEC104_MessageInfo messageInfo = IOA.get(i);
                        log.info("""
                                IOA：%d    Value：%f    QualityDescriptors：%d
                                """.formatted(messageInfo.getMessageAddress(), messageInfo.getValue().readFloat(), messageInfo.getQualityDescriptors()));
                    });
        }
    }
}
