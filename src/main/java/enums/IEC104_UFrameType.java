package enums;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public enum IEC104_UFrameType {
    // 启动激活
    STARTDT_ACT(0x07000000),
    // 启动确认
    STARTDT_CON(0x0B000000),
    // 停止激活
    STOPDT_ACT(0x13000000),
    // 停止确认
    STOPDT_CON(0x23000000),
    // 测试激活
    TESTFR_ACT(0x43000000),
    // 测试确认
    TESTFR_CON(0x83000000);

    private final int value;
    public static final Map<Integer, IEC104_UFrameType> U_CONTROL_MAP = new HashMap<>();

    IEC104_UFrameType(int value) {
        this.value = value;
    }

    static {
        for (IEC104_UFrameType type : values())
            U_CONTROL_MAP.put(type.value, type);
    }
}