package enums;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public enum IEC104TypeIdentifier {

    /**
     * 单点遥信
     */
    M_SP_NA_1(0x01, 1),

    /**
     * 带时标的单点遥信
     */
    M_SP_TA_1(0x02, 1),

    /**
     * 双点遥信
     */
    M_DP_NA_1(0x03, 1),

    /**
     * 带时标的双点遥信
     */
    M_DP_TA_1(0x04, 1),

    /**
     * 遥测，归一化值
     */
    M_ME_NA_1(0x09, 2),

    /**
     * 带时标的遥测，归一化值
     */
    M_ME_TA_1(0x0A, 2),

    /**
     * 遥测，标度化值
     */
    M_ME_NB_1(0x0B, 2),

    /**
     * 带时标的遥测，标度化值
     */
    M_ME_TB_1(0x0C, 2),

    /**
     * 遥测，短浮点数
     */
    M_ME_NC_1(0x0D, 2),

    /**
     * 带时标的遥测，短浮点数
     */
    M_ME_TC_1(0x0E, 2),

    /**
     * 单命令，遥控
     */
    C_SC_NA_1(0x2D, 1),

    /**
     * 双命令，遥控
     */
    C_DC_NA_1(0x2E, 1),

    /**
     * 遥调，归一化值
     */
    C_SE_NA_1(0x30, 2),

    /**
     * 遥调，标度化值
     */
    C_SE_NB_1(0x31, 2),

    /**
     * 遥调，短浮点数
     */
    C_SE_NC_1(0x32, 2),

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

    @Getter
    private final byte value;
    @Getter
    private final int msgLen;

    private static final Map<Byte, IEC104TypeIdentifier> valueTOEnums = new HashMap<>();

    static {
        for (IEC104TypeIdentifier type : IEC104TypeIdentifier.values()) {
            if (valueTOEnums.containsKey(type.value)) {
                throw new IllegalArgumentException("Duplicate value: " + type.value);
            }
            valueTOEnums.put(type.value, type);
        }
    }

    IEC104TypeIdentifier(int value, int msgLen) {
        this.value = (byte) value;
        this.msgLen = msgLen;
    }

    public static IEC104TypeIdentifier getIEC104TypeIdentifier(byte value) {
        IEC104TypeIdentifier result = valueTOEnums.get(value);
        if (result == null) return null;
        return result;
    }
}