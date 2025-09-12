package util;

import enums.IEC104_UFrameType;

import java.nio.ByteBuffer;
import java.util.concurrent.ScheduledFuture;

import static enums.IEC104_UFrameType.U_CONTROL_MAP;

public class IEC104Util {
    private static final int controlLength = 4;

    public static IEC104_UFrameType getUControlType(byte[] control) {
        if (control.length < controlLength || control[1] != 0 || control[2] != 0)
            return null;
        int key = ByteBuffer.wrap(control).getInt();
        return U_CONTROL_MAP.get(key);
    }

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

}
