package enums.monitoringDirections;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 品质位枚举类，用于表示数据质量的各种标志位
 * 每个枚举值对应一个特定的质量标志位
 */
@RequiredArgsConstructor
@Getter
public enum QualityBit {
    INVALID(7),     // bit7 → IV  无效标志
    NOT_CURRENT(6), // bit6 → NT  非当前值
    SUBSTITUTED(5), // bit5 → SB  人工取代
    BLOCKED(4),     // bit4 → BL  被封锁
    SPI_STATE_1(1), // bit1 → SPI 状态(双点遥信才有)
    OVERFLOW(0);    // bit0 → OV 溢出(遥测独有) | SPI 状态(遥信共有)

    private final int bitIndex;

    /**
     * 检查指定的品质字节中是否设置了特定的质量标志位
     *
     * @param quality 品质字节值
     * @param qb      要检查的质量标志位枚举
     * @return 如果设置了该标志位则返回true，否则返回false
     */
    public static boolean isSet(byte quality, QualityBit qb) {
        return (quality & (1 << qb.bitIndex)) != 0;
    }

    /**
     * 在品质字节中设置或清除指定的质量标志位
     *
     * @param quality 原始品质字节值
     * @param qb      要操作的质量标志位枚举
     * @param on      true表示设置该标志位，false表示清除该标志位
     * @return 操作后的品质字节值
     */
    public static byte set(byte quality, QualityBit qb, boolean on) {
        return on ? (byte) (quality | (1 << qb.bitIndex))
                : (byte) (quality & ~(1 << qb.bitIndex));
    }
}

