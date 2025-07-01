package core;

public class IEC104ThreadLocal {

    /**
     * 定时发送启动链路指令、测试链路指令
     */
    private static final ThreadLocal<ScheduledTaskPool> scheduledTaskPoolThreadLocal = new ThreadLocal<>();

    /**
     * 设置定时任务线程池
     *
     * @param scheduledTaskPool 需要绑定到当前线程的定时任务线程池实例
     */
    public static void setScheduledTaskPoolThreadLocal(ScheduledTaskPool scheduledTaskPool) {
        scheduledTaskPoolThreadLocal.set(scheduledTaskPool);
    }

    /**
     * 获取定时任务线程池, 如果不存在则创建
     *
     * @return 返回当前线程绑定的定时任务线程池实例
     */
    public static ScheduledTaskPool getScheduledTaskPool() {
        ScheduledTaskPool scheduledTaskPool = scheduledTaskPoolThreadLocal.get();
        if (scheduledTaskPool == null) {
            scheduledTaskPool = new ScheduledTaskPool();
            scheduledTaskPoolThreadLocal.set(scheduledTaskPool);
        }
        return scheduledTaskPool;
    }
}
