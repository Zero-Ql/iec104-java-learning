/*
 * IEC 60870-5-104 Protocol Implementation
 * Copyright (C) 2025 QSky
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */
package cloud.yunyat.model.master.handler;

import cloud.yunyat.model.impl.iec104.core.scheduler.IEC104_ScheduledTaskPool;
import cloud.yunyat.model.impl.iec104.enums.CauseOfTransmission;
import cloud.yunyat.model.impl.iec104.enums.IEC104_TypeIdentifier;
import cloud.yunyat.model.impl.iec104.frame.IEC104_MessageInfo;
import cloud.yunyat.model.impl.iec104.frame.asdu.IEC104_AsduMessageDetail;
import cloud.yunyat.model.impl.iec104.frame.asdu.IEC104_VSQ_COT_OA;
import cloud.yunyat.model.pojo.AnalogInput;
import cloud.yunyat.model.pojo.ParsedResult;
import cloud.yunyat.model.pojo.StatusInput;
import cloud.yunyat.model.service.MessageManager;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.log4j.Log4j2;
import cloud.yunyat.model.master.handler.parser.ParserRouter;
import cloud.yunyat.model.impl.iec104.util.ByteBufResource;

import java.util.List;
import java.util.NoSuchElementException;

@Log4j2
public class IEC104_iFrameMasterHandler extends SimpleChannelInboundHandler<IEC104_AsduMessageDetail> {

    // 获取接口路由实例
    ParserRouter parserRouter = ParserRouter.getInstance();

    // 获取消息管理实例
    MessageManager messageManager = MessageManager.getInstance();

    IEC104_TypeIdentifier typeIdentifier;

    boolean sq;
    short numIx;
    boolean test;
    boolean negative;
    short causeTx;
    byte senderAddress;
    short publicAddress;

    /**
     * @param ctx  通道上下文
     * @param asdu asdu对象
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, IEC104_AsduMessageDetail asdu) {
        if (asdu instanceof IEC104_AsduMessageDetail payload) {
            typeIdentifier = IEC104_TypeIdentifier.getIEC104TypeIdentifier(payload.getTypeIdentifier()).get();
            // 通过构建器创建 IEC104_VSQ_COT_OA 对象
            IEC104_VSQ_COT_OA vsqCotOa = new IEC104_VSQ_COT_OA.Builder(
                    payload.getVariableStructureQualifiers(),
                    payload.getTransferReason(),
                    payload.getSenderAddress()
            ).build();
            sq = vsqCotOa.isSQ();
            numIx = vsqCotOa.getNumIx();
            test = vsqCotOa.isTest();
            negative = vsqCotOa.isNegative();
            causeTx = vsqCotOa.getCauseTx();
            senderAddress = vsqCotOa.getSenderAddress();
            publicAddress = payload.getPublicAddress();
            List<IEC104_MessageInfo> IOA = payload.getIOA();
            if (IOA == null) {
                log.warn("IOA列表为空");
                return;
            }
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
                    typeIdentifier.getValue(), sq, numIx, test, negative, causeTx, senderAddress, publicAddress
            );
            log.info(headerLog);

            // 如果无法解析传送原因则抛出异常
            short cot = CauseOfTransmission.of(causeTx)
                    .orElseThrow(() -> new NoSuchElementException("无法解析的传送原因：" + causeTx))
                    .getCot();

//            try {
            // 通过类型标识和传送原因组合为一个唯一键，这个键对应一个唯一的IOA结构
            // 通过键获取对应的解析器
            IOA.forEach(info -> {
                ParsedResult parsedResult = parserRouter.lookup(typeIdentifier.getValue(), cot)
                        .parser(info.getMessageAddress(), ByteBufResource.of(info.getValue()), info.getQualityDescriptors(), ctx);
                // 收到I帧，取消T1，重置T3
                IEC104_ScheduledTaskPool.getFromChannel(ctx).onReceiveTestFRCon();
                dispatchAndPublish(parsedResult);
            });
//            } catch (NullPointerException e) {
//                log.error("无法解析的I帧(未找到对应解析器)：{}", payload);
//            }
        }
    }

    private void dispatchAndPublish(ParsedResult parsedResult) {
        if (parsedResult == null) return;
        if (isAnalogType(typeIdentifier)) {
            AnalogInput yc = new AnalogInput(
                    typeIdentifier,
                    parsedResult.getPoint(),
                    ((Number) parsedResult.getValue()).doubleValue(),
                    parsedResult.getQuality(),
                    parsedResult.getQualityBits());
            messageManager.publishYcData(publicAddress, yc);
        } else if (isStatusType(typeIdentifier)) {
            StatusInput yx = new StatusInput(
                    typeIdentifier,
                    parsedResult.getPoint(),
                    (Boolean) parsedResult.getValue(),
                    parsedResult.getQuality(),
                    parsedResult.getQualityBits()
            );
            messageManager.publishYxData(publicAddress, yx);
        }
    }

    // 辅助方法：判断是否为遥测类型
    private boolean isAnalogType(IEC104_TypeIdentifier type) {
        return type == IEC104_TypeIdentifier.M_ME_NC_1 ||
                type == IEC104_TypeIdentifier.M_ME_NA_1 ||
                type == IEC104_TypeIdentifier.M_ME_TF_1; // 按需补充
    }

    // 辅助方法：判断是否为遥信类型
    private boolean isStatusType(IEC104_TypeIdentifier type) {
        return type == IEC104_TypeIdentifier.M_SP_NA_1 ||
                type == IEC104_TypeIdentifier.M_DP_NA_1; // 按需补充
    }
}
