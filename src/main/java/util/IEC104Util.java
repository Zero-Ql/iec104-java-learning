package util;

import enums.IEC104_TypeIdentifier;
import enums.IEC104_VariableStructureQualifiers;
import frame.IEC104_MessageInfo;
import frame.asdu.IEC104_AsduMessageDetail;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.CorruptedFrameException;

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

        List<IEC104_MessageInfo> messagelist = decoderIoa(typeIdentifier, sq, NumIx, IOAList);

        return new IEC104_AsduMessageDetail.Builder(
                typeIdentifier,
                variableStructureQualifiers,
                transferReason,
                senderAddress,
                publicAddress,
                messagelist
        ).build();
    }

    private static List<IEC104_MessageInfo> decoderIoa(byte typeIdentifier, boolean sq, short num, ByteBuf ioaList) {

        List<IEC104_MessageInfo> meslist = new ArrayList<>();

        try {
            if (num <= 0 || num > 127) throw new IllegalArgumentException("num out of range: " + num);
            // 获取信息对象
            IEC104_TypeIdentifier messageObject = IEC104_TypeIdentifier.getIEC104TypeIdentifier(typeIdentifier);
            /*
              获取计算信息对象理论长度
              计算公式：
                  sq=true(连续)：3字节IOA头 + 读取的数量 x (理论信息对象值长度 + 质量码)
                  sq=false(不连续)：读取的数量 x (IOA地址 + 理论信息对象值长度 + 质量码)
             */
            int ioaLen = sq ? 3 + num * (messageObject.getMsgLen() + 1) : num * (messageObject.getMsgLen() + 4);

            // 实际读取长度如果不等于理论计算的长度
            if (ioaList.readableBytes() != ioaLen) {
                throw new CorruptedFrameException("""
                        IOA 总长度与理论计算长度不符：
                            理论总长度：%d
                            实际读取长度：%d
                        """.formatted(ioaLen, ioaList.readableBytes()));
            }

            // sq 为 true 连续
            if (sq) {
                // 读取第一个点号(3字节)
                int messageAddress = ioaList.readUnsignedMedium();
                if (messageAddress > Integer.MAX_VALUE - num) {
                    throw new CorruptedFrameException("""
                                IOA地址长度溢出：
                                    IOA地址：%d
                                    Integer上限：%d
                            """.formatted(messageAddress, Integer.MAX_VALUE - num));
                }
                for (int offset = messageAddress; offset < messageAddress + num; offset++) {
                    if (ioaList.readableBytes() < messageObject.getMsgLen() + 1) {

                        throw new CorruptedFrameException("""
                                可读字节不足一帧：
                                    可读字节长度：%d
                                    最低要求长度：%d
                                    完整报文：%s
                                """.formatted(ioaList.readableBytes(), messageObject.getMsgLen() + 1, ioaList.getBytes(ioaList.readerIndex(), new byte[ioaList.readableBytes()])));
                    }
                    // 读取信息对象值
                    ByteBuf value = ioaList.readBytes(messageObject.getMsgLen());
                    // 读取质量描述符
                    byte qualityDescriptors = ioaList.readByte();
                    meslist.add(createInfo(offset, value, qualityDescriptors, messageObject));
                }
            } else {
                for (int offset = 0; offset < num; offset++) {
                    if (ioaList.readableBytes() < 3 + messageObject.getMsgLen() + 1) {
                        throw new CorruptedFrameException("""
                                可读字节不足一帧：
                                    可读字节长度：%d
                                    最低要求长度：%d
                                    完整报文：%s
                                """.formatted(ioaList.readableBytes(), messageObject.getMsgLen() + 4, ioaList.getBytes(ioaList.readerIndex(), new byte[ioaList.readableBytes()])));
                    }
                    int messageAddress = ioaList.readUnsignedMedium();
                    ByteBuf value = ioaList.readBytes(messageObject.getMsgLen());
                    byte qualityDescriptors = ioaList.readByte();
                    meslist.add(createInfo(messageAddress, value, qualityDescriptors, messageObject));
                }
            }
            return meslist;
        } finally {
            if (ioaList.refCnt() > 0) {
                ioaList.release();
            }
        }
    }

    private static IEC104_MessageInfo createInfo(int messageAddressAuto, ByteBuf value, byte qualityDescriptors, IEC104_TypeIdentifier messageObject) {
        try {
            IEC104_MessageInfo info = new IEC104_MessageInfo(
                    messageAddressAuto,
                    // 通过读取的 typeId 获取对应的信息对象，通过读取的 qualityDescriptors 及信息对象获取对应质量描述符
                    IEC104_VariableStructureQualifiers.getQualifiers(messageObject, qualityDescriptors).getQualityDescriptors()
            );
            info.setValue(value);
            return info;
        } catch (Exception e) {
            throw new RuntimeException(("""
                    获取质量描述符异常:
                        typeId：%s
                        IOA地址：%d
                        值：%s
                        质量描述符：%s
                    """).formatted(messageObject, messageAddressAuto, value, qualityDescriptors), e);
        }
    }
}
