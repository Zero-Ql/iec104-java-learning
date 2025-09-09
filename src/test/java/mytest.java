import org.ini4j.Ini;
import org.junit.Test;
import util.ByteUtil;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

import static enums.IEC104_UFrameType.U_CONTROL_MAP;

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
        System.out.println(U_CONTROL_MAP.get(key));
        System.out.println((bytes[0] & 0x003) == 0x003);
    }

    @Test
    public void demo4() {
        // TODO 计算接收发送序号
//        new b().test1();
        short tx = -1;
        int rx_int = 65534;
        byte[] bytes = ByteUtil.intToByte(rx_int);
        byte[] rx_bytes = new byte[]{bytes[2], bytes[3]};
        System.out.println(rx_bytes);
    }

    @Test
    public void demo5(){
        try {
            Ini ini = new Ini(new File("src/test/resources/piec104.ini"));
            var t1 = ini.get("test", "t1");
            System.out.println(t1);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}


















