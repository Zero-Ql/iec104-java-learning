package enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor
public enum IEC104_TypeIdentifier {

    /**
     * 单点遥信
     */
    M_SP_NA_1((byte) 0x01, 1),

    /**
     * 带时标的单点遥信
     */
    M_SP_TB_1((byte) 0x1E, 1),

    /**
     * 双点遥信
     */
    M_DP_NA_1((byte) 0x03, 1),

    /**
     * 带时标的双点遥信
     */
    M_DP_TB_1((byte) 0x1F, 1),

    /**
     * 遥测，归一化值
     */
    M_ME_NA_1((byte) 0x09, 2),

    /**
     * 带时标的遥测，归一化值
     */
    M_ME_TD_1((byte) 0x22, 2),

    /**
     * 遥测，标度化值
     */
    M_ME_NB_1((byte) 0x0B, 2),

    /**
     * 带时标的遥测，标度化值
     */
    M_ME_TE_1((byte) 0x35, 2),

    /**
     * 遥测，短浮点数
     */
    M_ME_NC_1((byte) 0x0D, 4),

    /**
     * 带时标的遥测，短浮点数
     */
    M_ME_TF_1((byte) 0x36, 4),

    /**
     * 单命令，遥控
     */
    C_SC_NA_1((byte) 0x2D, 1),

    /**
     * 带时标的遥控，单命令
     */
    C_SC_TA_1((byte) 0x3A, 1),

    /**
     * 双命令，遥控
     */
    C_DC_NA_1((byte) 0x2E, 1),

    /**
     * 带时标的遥控，双命令
     */
    C_DC_TA_1((byte) 0x3B, 1),

    /**
     * 遥调，归一化值
     */
    C_SE_NA_1((byte) 0x30, 2),

    /**
     * 带时标的遥调，归一化值
     */
    C_SE_TA_1((byte) 0x3D, 2),

    /**
     * 遥调，标度化值
     */
    C_SE_NB_1((byte) 0x31, 2),

    /**
     * 带时标的遥调，标度化值
     */
    C_SE_TB_1((byte) 0x3E, 2),

    /**
     * 遥调，短浮点数
     */
    C_SE_NC_1((byte) 0x32, 2),

    /**
     * 带时标的遥调，短浮点数
     */
    C_SE_TC_1((byte) 0x3F, 2),

    /**
     * 总召唤
     */
    C_IC_NA_1((byte) 0x64, 0),

    /**
     * 时钟同步命令
     */
    C_CS_NA_1((byte) 0x67, 1),

    /**
     * 复位进程命令
     */
    C_RP_NA_1((byte) 0x69, 1);

    /**
     * 类型标识
     */
    private final byte value;
    /**
     * NVA 长度
     */
    private final int msgLen;

    private static final Map<Byte, IEC104_TypeIdentifier> VALUE_MAP = Stream.of(
            values()).collect(Collectors.toMap(IEC104_TypeIdentifier::getValue, Function.identity()));

    public static Optional<IEC104_TypeIdentifier> getIEC104TypeIdentifier(byte value) {
        return Optional.ofNullable(VALUE_MAP.get(value));
    }
}