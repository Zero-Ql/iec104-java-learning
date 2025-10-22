package master.handler;

import enums.CauseOfTransmission;
import enums.IEC104_TypeIdentifier;
import frame.IEC104_MessageInfo;
import frame.asdu.IEC104_AsduMessageDetail;
import frame.asdu.IEC104_VSQ_COT_OA;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.log4j.Log4j2;

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
            // 如果是总召响应
            if (IEC104_TypeIdentifier.getIEC104TypeIdentifier(typeIdentifier)
                    .orElseThrow(() -> new NoSuchElementException("无法解析的类型标识：" + typeIdentifier))
                    .getValue() == IEC104_TypeIdentifier.C_IC_NA_1.getValue() &&
                    CauseOfTransmission.of(causeTx).orElseThrow().getCot() == CauseOfTransmission.ACT_CON.getCot()) {

            }
            parserYc(IOA);
        }
    }

    /**
     * @param IOA 遥测IOA列表
     */
    private void parserYc(List<IEC104_MessageInfo> IOA) {
        if (IOA == null) {
            return;
        }
        for (IEC104_MessageInfo messageInfo : IOA) {
            try {
                final ByteBuf value = ReferenceCountUtil.releaseLater(messageInfo.getValue());
                if (value != null)
                    log.info("IOA：{}    Value：{}    QualityDescriptors：{} ",
                            messageInfo.getMessageAddress(),
                            value.readFloat(),
                            messageInfo.getQualityDescriptors());
            } finally {
                final ByteBuf value = ReferenceCountUtil.releaseLater(messageInfo.getValue());
                if (value != null) value.release();
            }
        }
    }
}
