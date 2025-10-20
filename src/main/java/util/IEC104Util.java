package util;

import enums.IEC104_TypeIdentifier;
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

        // 获取 SQ 值
        boolean sq = (variableStructureQualifiers & 0x80) != 0;
        // 获取信息体对象数量
        short NumIx = (short) (variableStructureQualifiers & (~(1 << 7)));

        // 传送原因
        byte transferReason = payload.readByte();
        // 发送方地址
        byte senderAddress = payload.readByte();
        // 公共地址
        short publicAddress = payload.readShort();

        // 切割剩余字节（当前版本 slice 不会增加引用计数）
        ByteBuf IOAList = payload.slice();

        // TODO 解析 IOA
        List<IEC104_MessageInfo> meslist = decoderIoa(typeIdentifier, sq, NumIx, IOAList);

        new IEC104_AsduMessageDetail.Builder(

        )
        return;
    }

    public static List<IEC104_MessageInfo> decoderIoa(byte typeIdentifier, boolean sq, short num, ByteBuf ioaList) {

        List<IEC104_MessageInfo> meslist = new ArrayList<>();

        // 获取剩余字节长度
        int len = ioaList.readableBytes();

        // sq 为 true 连续
        if (sq) {
            // 获取信息对象
            IEC104_TypeIdentifier messageObject = IEC104_TypeIdentifier.getIEC104TypeIdentifier(typeIdentifier);
            // 获取计算信息对象数量
            int ioaNum = len / messageObject.getMsgLen();

            // NumIx 必须等于实际计算的数量
            if (num == ioaNum) {
                // 读取第一个点号(3字节)
                int messageAddress = ioaList.readUnsignedMedium();
                for (int messageAddressAuto = messageAddress; messageAddressAuto < messageAddress + num; messageAddressAuto++) {
                    // 读取信息对象值
                    ByteBuf value = ioaList.readBytes(messageObject.getMsgLen());
                    // 读取质量描述符
                    byte qualityDescriptors = ioaList.readByte();

                    IEC104_MessageInfo info = new IEC104_MessageInfo(
                            messageAddressAuto,
                            // 通过读取的 typeId 获取对应的信息对象，通过读取的 qualityDescriptors 及信息对象获取对应质量描述符
                            IEC104_VariableStructureQualifiers.getQualifiers(messageObject, qualityDescriptors).getQualityDescriptors()
                    );
                    info.setValue(value);
                    meslist.add(info);
                }
            }
        }
        return meslist;
    }

}
