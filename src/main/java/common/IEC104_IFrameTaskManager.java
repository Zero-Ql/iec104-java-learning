package common;

import config.piec104Config;
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
import util.ByteUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicReference;

public class IEC104_IFrameTaskManager {

    private final IEC104_ScheduledTaskPool iEC104_ScheduledTaskPool;
    private final ChannelHandlerContext ctx;
    private final ScheduledExecutorService executor;
    private final piec104Config config;

    private final AtomicReference<ScheduledFuture<?>> interrogationCommandTask = new AtomicReference<>();

    private final IEC104_FrameBuilder iEC104_FrameParser;

    public IEC104_IFrameTaskManager(IEC104_ScheduledTaskPool iEC104_ScheduledTaskPool, ChannelHandlerContext ctx, ScheduledExecutorService executor, piec104Config config, IEC104_FrameBuilder iEC104_FrameParser) {
        this.iEC104_ScheduledTaskPool = iEC104_ScheduledTaskPool;
        this.ctx = ctx;
        this.executor = executor;
        this.config = config;
        this.iEC104_FrameParser = iEC104_FrameParser;
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

        IEC104_ApciMessageDetail apciMessageDetail = new IEC104_ApciMessageDetail();
        List<frame.IEC104_MessageInfo> ioa = new ArrayList<>();

        // 通过原子引用获取当前任务
        ScheduledFuture<?> currentTask = interrogationCommandTask.get();

        ioa.add(new IEC104_MessageInfo(0, IEC104_VariableStructureQualifiers.C_IC_NA_1_QUALIFIER.getValue()));
        apciMessageDetail.setIEC104_controlField(new byte[]{0x00, 0x00, 0x00, 0x00});

        IEC104_FrameBuilder iFrame = buildFrames(apciMessageDetail, ByteUtil.customStructureToBytes(sq, numIx, negative, test, causeTx, senderAddress), publicAddress, ioa);

        // 使用 PooledByteBufAllocator 优化内存池化
        ByteBufAllocator allocator = ctx.alloc();

        // 合并可预测大小的字段
        // 1. APCI 字段（假设控制字段为 4 字节）
        ByteBuf apcibuf = allocator.buffer(4);
        apcibuf.writeBytes(iFrame.getApciMessageDetail().getIEC104_controlField());

        // 2. ASDU 头部字段（4 字节：Type ID + VSQ + Transfer Reason + Sender Address）
        ByteBuf asduHeader = allocator.buffer(4);
        asduHeader.writeByte(iFrame.getAsduMessageDetail().getTypeIdentifier());
        asduHeader.writeByte(iFrame.getAsduMessageDetail().getVariableStructureQualifiers());
        asduHeader.writeByte(iFrame.getAsduMessageDetail().getTransferReason());
        asduHeader.writeByte(iFrame.getAsduMessageDetail().getSenderAddress());

        // 3. 公共地址 + QOI（3 字节：Public Address 2 字节 + QOI 1 字节）
        ByteBuf commonInfo = allocator.buffer(3);
        commonInfo.writeShort(iFrame.getAsduMessageDetail().getPublicAddress());
        commonInfo.writeByte(iFrame.getAsduMessageDetail().getIOA().getFirst().getVariableStructureQualifiers());

        // 4. IOA 地址（3 字节：使用 writeMedium 写入 3 字节整数）
        ByteBuf ioaAddress = allocator.buffer(3);
        ioaAddress.writeMedium(iFrame.getAsduMessageDetail().getIOA().getFirst().getMessageAddress());

        // 使用 CompositeByteBuf 拼接
        CompositeByteBuf composite = allocator.compositeBuffer();
        composite.addComponent(true, apcibuf);       // 4 字节
        composite.addComponent(true, asduHeader);    // 4 字节
        composite.addComponent(true, commonInfo);    // 3 字节
        composite.addComponent(true, ioaAddress);    // 3 字节

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
