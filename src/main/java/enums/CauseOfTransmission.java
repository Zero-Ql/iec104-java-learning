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
public enum CauseOfTransmission {


    /**
     * 周期/循环
     */
    PER_CYC((short) 0x01),

    /**
     * 背景扫描
     */
    BACK((short) 0x02),

    /**
     * 突变
     */
    SPONT((short) 0x03),

    /**
     * 被请求
     */
    REQ((short) 0x05),



    // 总召传送原因
    /**
     * 激活
     */
    ACT((short) 0x06),
    /**
     * 激活确认
     */
    ACT_CON((short) 0x07),
    /**
     * 停止激活
     */
    DE_ACT((short) 0x08),
    /**
     * 停止激活确认
     */
    DE_ACT_CON((short) 0x09),
    /**
     * 激活终止
     */
    ACT_TERM((short) 0x0A),


    // 公共传送原因
    /**
     * 响应总召唤
     */
    INTROGEN((short) 0x14);

    /**
     * 传输原因标识符 (Cause of Transmission)
     */
    private final short cot;

    private static final Map<Short, CauseOfTransmission> INDEX = Stream.of(
            values()).collect(
            Collectors.toMap(CauseOfTransmission::getCot, Function.identity()));

    public static Optional<CauseOfTransmission> of(short cot) {
        return Optional.ofNullable(INDEX.get(cot));
    }
}
