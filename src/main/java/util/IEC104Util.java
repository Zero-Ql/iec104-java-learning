package util;

import enums.IEC104_VariableStructureQualifiers;
import frame.IEC104_MessageInfo;
import frame.asdu.IEC104_AsduMessageDetail;
import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

public class IEC104Util {
    /**
     * 取消指定的定时任务
     * <p>
     * 检查任务是否存在且未完成，如果满足条件则取消该任务
     *
     * @param task 需要取消的定时任务
     */
    public static void isCancel(ScheduledFuture<?> task) {
        // 检查任务是否存在且未完成
        if (task != null && !task.isDone()) {
            // 取消超时任务
            task.cancel(false);
        }
    }

    public static IEC104_AsduMessageDetail decodeAsdu(ByteBuf payload) {

        // 类型标识符
        byte typeIdentifier = payload.readByte();
        // 可变结构限定词
        byte variableStructureQualifiers = payload.readByte();
        // 传送原因
        byte transferReason = payload.readByte();
        // 发送方地址
        byte senderAddress = payload.readByte();
        // 公共地址
        short publicAddress = payload.readShort();

        List<IEC104_MessageInfo> IOA = new ArrayList<>();

        // TODO 解析 IOA
        if (~(variableStructureQualifiers >> 7) == 0) {
            // 获取信息体对象数量
            short NumIx = (short) (variableStructureQualifiers & (~(1 << 7)));
        }

        while (payload.isReadable()) {
            // TODO 如何读取IOA
            if (IEC104_VariableStructureQualifiers.getQualifiers())
        }

        new IEC104_AsduMessageDetail.Builder(

        )
    }

}
