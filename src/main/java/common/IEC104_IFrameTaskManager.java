package common;

import core.scheduler.IEC104_ScheduledTaskPool;
import enums.IEC104_TypeIdentifier;
import frame.IEC104_FrameBuilder;
import frame.IEC104_MessageInfo;
import frame.apci.IEC104_ApciMessageDetail;
import frame.asdu.IEC104_AsduMessageDetail;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

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
        IEC104_ApciMessageDetail apciMessageDetail = new IEC104_ApciMessageDetail((short) 0x00, (short) 0x00);
        // sq + numIx IOA数量
        byte sqNumIx = 0x01;
        // test 测试标识 + negative 否定标识
        // causeTx 传送原因
        byte causeTx = 6;
        // OA 发送方地址
        byte oa = 0;
        // 公共地址
        short ca = 1;

        List<IEC104_MessageInfo> ioa = new ArrayList<>();

        ioa.add(new IEC104_MessageInfo(0, (byte) 0x14));

        // 构建帧对象
        IEC104_FrameBuilder iFrame = new IEC104_FrameBuilder.Builder(apciMessageDetail).setAsduMessageDetail(
                        new IEC104_AsduMessageDetail.Builder(
                                IEC104_TypeIdentifier.C_IC_NA_1.getValue(),
                                sqNumIx,
                                causeTx,
                                oa,
                                ca,
                                ioa).build())
                .build();
        log.info("发送总召：{}", iFrame);
        // 发送总召
        ctx.write(iFrame);
    }
}
