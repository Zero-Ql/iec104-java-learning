import org.junit.Test;//
import config.Piec104Config;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class mytest {
    @Test
    public void demo1() {
        int value = 0;
        byte[] bytes = new byte[]{(byte) 0x12, (byte) 0x34, (byte) 0x56, (byte) 0x78};
        for (int i = 0; i < 4; i++) {
            int shift = (3 - i) * 8;
            value += (bytes[i] & 0xFF) << shift;
        }
        System.out.println(value);
    }

    @Test
    public void demo2() {
        int i = 0x12345678;
        byte[] bytes = new byte[4];
        bytes[0] = (byte) ((i >> 24) & 0xFF);
        bytes[1] = (byte) ((i >> 16) & 0xFF);
        bytes[2] = (byte) ((i >> 8) & 0xFF);
        bytes[3] = (byte) (i & 0xFF);
        System.out.println(Arrays.toString(bytes));
    }

    /**
     * 演示U帧控制域解析测试
     * <p>
     * 该方法用于测试U帧控制域的解析逻辑，包括通过ByteBuffer获取整数值和位运算检查
     */
    @Test
    public void demo3() {
        byte[] bytes = new byte[]{(byte) 0x83, (byte) 0x00, (byte) 0x00, (byte) 0x00};
        int key = ByteBuffer.wrap(bytes).getInt();
//        System.out.println(U_CONTROL_MAP.get(key));
        System.out.println((bytes[0] & 0x003) == 0x003);
    }

    @Test
    public void demo4() {
//        new b().test1();

        // 发送
        byte tx_1 = (byte) ((65534 >> 8) & 0xFF);
        byte tx_2 = (byte) (65534 & 0xFF);

        int test = 16;
        test |= (1 << 7); //置1
//        test &= ~(1 << 7); //清0
        // 接收
        int rx = ((tx_1 & 0xFF) << 8) | (tx_2 & 0xFF);
        System.out.println(rx);

        System.out.println(test);
    }

    @Test
    public void demo5() {
        Piec104Config cfg = Piec104Config.getInstance();
        System.out.println(cfg.getT2() + " " + cfg.getW());
    }

    /**
     * IEC104_FrameBuilder 单元测试
     */
    @Test
    public void demo6() {
//
//        List<IEC104_MessageInfo> ioa = new ArrayList<>();
//        var iEC104_apciMessageDetail = new IEC104_ApciMessageDetail();
//        boolean sq = false;
//        short numIx = 1;
//
//        boolean negative = false;
//        boolean test = false;
//        short causeTx = 6;
//
//        byte variableStructureQualifiers;
//        byte transferReason;
//
//        byte senderAddress = 0;
//        short publicAddress = 1;
//
//        ioa.add(new IEC104_MessageInfo(0, IEC104_VariableStructureQualifiers.C_IC_NA_1_QUALIFIER.getValue()));
//        iEC104_apciMessageDetail.setIEC104_controlField(new byte[]{0x00, 0x00, 0x00, 0x00});
//
//        if (sq) {
//            numIx |= (1 << 7);
//        } else {
//            numIx &= ~(1 << 7);
//        }
//
//        if (negative) {
//            causeTx |= (1 << 7);
//            if (test) {
//                causeTx |= (1 << 6);
//            } else {
//                causeTx &= ~(1 << 6);
//            }
//        } else {
//            causeTx &= ~(1 << 7);
//        }
//
//        variableStructureQualifiers = (byte) numIx;
//        transferReason = (byte) causeTx;
//
//        var iEC104_asduMessageDetail = new IEC104_AsduMessageDetail.Builder(
//                IEC104_TypeIdentifier.C_IC_NA_1.getValue(),
//                variableStructureQualifiers,
//                transferReason,
//                senderAddress,
//                publicAddress,
//                ioa).build();
//
//        var iEC104_FrameBuilder = new IEC104_FrameBuilder.Builder(iEC104_apciMessageDetail).setAsduMessageDetail(iEC104_asduMessageDetail).build();
//        System.out.println(iEC104_FrameBuilder);
    }

    @Test
    public void demo7() {
        ByteBuf buf = PooledByteBufAllocator.DEFAULT.buffer(8);
        System.out.println("after alloc: " + buf.refCnt()); // 1

        ByteBuf a = buf.slice();
        System.out.println("after 1st slice：" + a.refCnt());

        buf.release();
        System.out.println("after 2nd release: " + a.refCnt()); // 0 → 内存已回收
    }

    @Test
    public void demo8() throws IOException {
        System.out.println((short) ((byte) 0x81 & (byte) 0x80));
    }

    @Test
    public void demo9() {
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(2);
        final AtomicReference<ScheduledFuture<?>> a = new AtomicReference<>();
        final AtomicReference<ScheduledFuture<?>> b = new AtomicReference<>();

        ScheduledFuture<?> task = executorService.schedule(() -> {
            System.out.println("task");
        }, 1, TimeUnit.SECONDS);

        b.set(task);

        System.out.println(!a.compareAndSet(null, b.get()));
    }
}
