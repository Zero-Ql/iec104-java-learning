package common;

import config.piec104Config;
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

    private static final Logger log = LogManager.getLogger(IEC104_IFrameTaskManager.class);
    private final ChannelHandlerContext ctx;
    private final ScheduledExecutorService executor;
    private final piec104Config config;

    private final AtomicReference<ScheduledFuture<?>> interrogationCommandTask = new AtomicReference<>();

    public IEC104_IFrameTaskManager(ChannelHandlerContext ctx, ScheduledExecutorService executor, piec104Config config) {
        this.ctx = ctx;
        this.executor = executor;
        this.config = config;
    }

    public void sendIFrame() {

    }

    public void sendInterrogationCommand() {

        byte H = 0x68;

        short L = 0;

        boolean sq = false;
        short numIx = 1;

        boolean negative = false;
        boolean test = false;
        short causeTx = 6;

        byte senderAddress = 0;
        short publicAddress = 1;

        IEC104_ApciMessageDetail apciMessageDetail = new IEC104_ApciMessageDetail();
        List<IEC104_MessageInfo> ioa = new ArrayList<>();

        // 通过原子引用获取当前任务
        ScheduledFuture<?> currentTask = interrogationCommandTask.get();

        ioa.add(new IEC104_MessageInfo(0, IEC104_VariableStructureQualifiers.C_IC_NA_1_QUALIFIER.getValue()));
        apciMessageDetail.setIEC104_controlField(new byte[]{0x00, 0x00, 0x00, 0x00});

        // 构建帧对象
        IEC104_FrameBuilder iFrame = buildFrames(apciMessageDetail, ByteUtil.customStructureToBytes(sq, numIx, negative, test, causeTx, senderAddress), publicAddress, ioa);

        // 使用 PooledByteBufAllocator 优化内存池化
        ByteBufAllocator allocator = ctx.alloc();

        // 合并可预测大小的字段
        // APCI 字段（控制字段为 4 字节）
        ByteBuf apcibuf = allocator.buffer(4);
        apcibuf.writeBytes(iFrame.getApciMessageDetail().getIEC104_controlField());

        // ASDU 头部字段（4 字节：Type ID + VSQ + Transfer Reason + Sender Address）
        ByteBuf asduHeader = allocator.buffer(4);
        asduHeader.writeByte(iFrame.getAsduMessageDetail().getTypeIdentifier());
        asduHeader.writeByte(iFrame.getAsduMessageDetail().getVariableStructureQualifiers());
        asduHeader.writeByte(iFrame.getAsduMessageDetail().getTransferReason());
        asduHeader.writeByte(iFrame.getAsduMessageDetail().getSenderAddress());

        // 公共地址（2 字节：Public Address 2 字节）
        ByteBuf commonInfo = allocator.buffer(2);
        byte [] byteAddress = ByteUtil.shortToByte(iFrame.getAsduMessageDetail().getPublicAddress());
        // 将 short 转为字节数组后转为 小端序 写入
        commonInfo.writeByte(byteAddress[1]);
        commonInfo.writeByte(byteAddress[0]);

        // IOA 地址（3 字节：使用 writeMedium 写入 3 字节整数）
        ByteBuf ioaAddress = allocator.buffer(3);
        ioaAddress.writeMedium(iFrame.getAsduMessageDetail().getIOA().getFirst().getMessageAddress());

        // QOI（1 字节：QualityDescriptors）
        ByteBuf qoi = allocator.buffer(1);
        qoi.writeByte(iFrame.getAsduMessageDetail().getIOA().getFirst().getQualityDescriptors());

        List<ByteBuf> bufs = Arrays.asList(apcibuf, asduHeader, commonInfo, ioaAddress, qoi);

        for (ByteBuf b : bufs) {
            L += (short) b.readableBytes();
        }

        ByteBuf apciHeader = allocator.buffer(2);
        apciHeader.writeByte(H);
        apciHeader.writeByte((byte) (L & 0xFF));

        // 使用 CompositeByteBuf 拼接
        CompositeByteBuf composite = allocator.compositeBuffer();
        composite.addComponent(apciHeader);
        composite.addComponent(apcibuf);       // 4 字节
        composite.addComponent(asduHeader);    // 4 字节
        composite.addComponent(commonInfo);    // 2 字节
        composite.addComponent(ioaAddress);    // 3 字节
        composite.addComponent(qoi);    // 1 字节

        composite.writerIndex(composite.capacity());


        // 防止重复发送，如果任务已完成或未提交过则跳过
//        if (currentTask != null && !currentTask.isDone()) {
//            return;
//        }

        log.info("发送 {} 启动帧", composite);
        // 发送总召
        ctx.writeAndFlush(composite);

        // 提交任务并开启 T1 计时器
        ScheduledFuture<?> newTask = executor.schedule(() -> {
            try {
                if (ctx.channel().isActive()) {
                    log.warn("总召超时，关闭连接");
                    ctx.close();
                }
            } catch (Exception e) {
                log.error("执行超时任务异常", e);
            }
        }, Long.parseLong(config.getT1()), TimeUnit.SECONDS);

        // 使用 CAS乐观锁非阻塞更新
        interrogationCommandTask.compareAndSet(currentTask, newTask);

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
