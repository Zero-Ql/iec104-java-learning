package common;

import config.Piec104Config;
import core.scheduler.IEC104_ScheduledTaskPool;
import enums.IEC104_TypeIdentifier;
import enums.IEC104_VariableStructureQualifiers;
import frame.IEC104_FrameBuilder;
import frame.IEC104_MessageInfo;
import frame.apci.IEC104_ApciMessageDetail;
import frame.asdu.IEC104_AsduMessageDetail;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.CompositeByteBuf;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import util.ByteUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class IEC104_IFrameTaskManager {

    private final IEC104_ScheduledTaskPool parent;
    private final ChannelHandlerContext ctx;

    private static final Logger log = LogManager.getLogger(IEC104_IFrameTaskManager.class);

    public IEC104_IFrameTaskManager(IEC104_ScheduledTaskPool parent, ChannelHandlerContext ctx) {
        this.parent = parent;
        this.ctx = ctx;
    }

    public void sendIFrame() {

    }

    public void sendInterrogationCommand() {

        boolean sq = false;
        short numIx = 1;

        boolean negative = false;
        boolean test = false;
        short causeTx = 6;

        byte senderAddress = 0;
        short publicAddress = 1;

        IEC104_ApciMessageDetail apciMessageDetail = new IEC104_ApciMessageDetail((short) 0x00, (short) 0x00);
        List<IEC104_MessageInfo> ioa = new ArrayList<>();

        ioa.add(new IEC104_MessageInfo(0, IEC104_VariableStructureQualifiers.getQualifiers(IEC104_TypeIdentifier.C_IC_NA_1.getValue(), 0x00)));

        // 构建帧对象
        IEC104_FrameBuilder iFrame = buildFrames(apciMessageDetail, ByteUtil.customStructureToBytes(sq, numIx, negative, test, causeTx, senderAddress), publicAddress, ioa);

        log.info("发送 {} 启动帧", iFrame);
        // 发送总召
        ctx.write(iFrame);
    }

    private IEC104_FrameBuilder buildFrames(IEC104_ApciMessageDetail apciMessageDetail, byte[] result, short publicAddress, List<IEC104_MessageInfo> ioa) {
        var iEC104_asduMessageDetail = new IEC104_AsduMessageDetail.Builder(
                // typeId 类型标识
                IEC104_TypeIdentifier.C_IC_NA_1.getValue(),
                // sq + numIx IOA数量
                result[0],
                // test 测试标识 + negative 否定标识 + causeTx 传送原因
                result[1],
                // OA 发送方地址
                result[2],
                // 公共地址
                publicAddress,
                // 信息对象列表
                ioa).build();

        return new IEC104_FrameBuilder.Builder(apciMessageDetail).setAsduMessageDetail(iEC104_asduMessageDetail).build();
    }

}
