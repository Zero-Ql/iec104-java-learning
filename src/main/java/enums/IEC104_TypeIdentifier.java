package enums;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public enum IEC104_TypeIdentifier {

    /**
     * 单点遥信
     */
    M_SP_NA_1(0x01, 1),

    /**
     * 带时标的单点遥信
     */
    M_SP_TB_1(0x1E, 1),

    /**
     * 双点遥信
     */
    M_DP_NA_1(0x03, 1),

    /**
     * 带时标的双点遥信
     */
    M_DP_TB_1(0x1F, 1),

    /**
     * 遥测，归一化值
     */
    M_ME_NA_1(0x09, 2),

    /**
     * 带时标的遥测，归一化值
     */
    M_ME_TD_1(0x22, 2),

    /**
     * 遥测，标度化值
     */
    M_ME_NB_1(0x0B, 2),

    /**
     * 带时标的遥测，标度化值
     */
    M_ME_TE_1(0x35, 2),

    /**
     * 遥测，短浮点数
     */
    M_ME_NC_1(0x0D, 2),

    /**
     * 带时标的遥测，短浮点数
     */
    M_ME_TF_1(0x36, 2),

    /**
     * 单命令，遥控
     */
    C_SC_NA_1(0x2D, 1),

    /**
     * 带时标的遥控，单命令
     */
    C_SC_TA_1(0x3A, 1),

    /**
     * 双命令，遥控
     */
    C_DC_NA_1(0x2E, 1),

    /**
     * 带时标的遥控，双命令
     */
    C_DC_TA_1(0x3B, 1),

    /**
     * 遥调，归一化值
     */
    C_SE_NA_1(0x30, 2),

    /**
     * 带时标的遥调，归一化值
     */
    C_SE_TA_1(0x3D, 2),

    /**
     * 遥调，标度化值
     */
    C_SE_NB_1(0x31, 2),

    /**
     * 带时标的遥调，标度化值
     */
    C_SE_TB_1(0x3E, 2),

    /**
     * 遥调，短浮点数
     */
    C_SE_NC_1(0x32, 2),

    /**
     * 带时标的遥调，短浮点数
     */
    C_SE_TC_1(0x3F, 2),

    /**
     * 总召唤
     */
    C_IC_NA_1(0x64, 1),

    /**
     * 时钟同步命令
     */
    C_CS_NA_1(0x67, 1),

    /**
     * 复位进程命令
     */
    C_RP_NA_1(0x69, 1);

    /**
     * 类型标识
     */
    @Getter
    private final byte value;
    /**
     * NVA 长度
     */
    @Getter
    private final int msgLen;

    private static final Map<Byte, IEC104_TypeIdentifier> valueTOEnums = new HashMap<>();

    /*
      检查 value 是否已经在 valueTOEnums 中，如果在抛出异常，否则添加
     */
    static {
        for (IEC104_TypeIdentifier type : IEC104_TypeIdentifier.values()) {
            if (valueTOEnums.containsKey(type.value)) {
                throw new IllegalArgumentException("Duplicate value: " + type.value);
            }
            valueTOEnums.put(type.value, type);
        }
    }

    IEC104_TypeIdentifier(int value, int msgLen) {
        this.value = (byte) value;
        this.msgLen = msgLen;
    }

    public static IEC104_TypeIdentifier getIEC104TypeIdentifier(byte value) {
        return valueTOEnums.get(value);
    }
}