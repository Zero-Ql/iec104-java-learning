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
