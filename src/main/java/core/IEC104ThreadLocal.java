//package core;
//
//import io.netty.util.AttributeKey;
//
//public class IEC104ThreadLocal {
//
//    /**
//     * 定义一个线程局部变量，用于存储每个线程独立的 ScheduledTaskPool 实例
//     */
//    private static final AttributeKey<ScheduledTaskPool> scheduledTaskPoolThreadLocal = AttributeKey.valueOf("scheduledTaskPool");
//
//    /**
//     * 设置定时任务线程池
//     *
//     * @param scheduledTaskPool 需要绑定到当前线程的定时任务线程池实例
//     */
//    public static void setScheduledTaskPoolThreadLocal(ScheduledTaskPool scheduledTaskPool) {
//        scheduledTaskPoolThreadLocal.set(scheduledTaskPool);
//    }
//
//    /**
//     * 获取定时任务线程池, 如果不存在则创建
//     *
//     * @return 返回当前线程绑定的定时任务线程池实例
//     */
//    public static ScheduledTaskPool getScheduledTaskPool() {
//        return scheduledTaskPoolThreadLocal.get();
//    }
//
//
//}
