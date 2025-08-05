package core;

import common.IEC104_BasicInstructions;
import config.piec104Config;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 定时任务池管理类
 * <p>
 * 负责管理IEC104协议中的定时任务，包括链路启动超时检测等
 * 与Netty的ChannelHandlerContext绑定，确保每个连接拥有独立的定时任务池
 *
 * @author QSky
 */
public class IEC104_ScheduledTaskPool {

    private static final Logger log = LogManager.getLogger(IEC104_ScheduledTaskPool.class);

    /**
     * 通道处理器上下文对象
     */
    private final ChannelHandlerContext ctx;

    /**
     * 保留2个线程的线程池对象
     */
    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);

    /**
     * 启动任务的原子引用
     */
    private final AtomicReference<ScheduledFuture<?>> startTask = new AtomicReference<>();


    /**
     * 测试任务的原子引用
     */
    private final AtomicReference<ScheduledFuture<?>> testTask = new AtomicReference<>();

    private final AtomicReference<ScheduledFuture<?>> t1Task = new AtomicReference<>();
    private final AtomicReference<ScheduledFuture<?>> t3Task = new AtomicReference<>();

    /**
     * 获取通道配置实例
     */
    private static final piec104Config channelTimeOut = new piec104Config();

    /**
     * ScheduledTaskPool在Channel属性中的键值
     */
    private static final AttributeKey<IEC104_ScheduledTaskPool> SCHEDULED_TASK_POOL_ATTRIBUTE_KEY =
            AttributeKey.valueOf("scheduledTaskPool");

    /**
     * 链路是否已启动标志
     */
    private volatile boolean started = false;

    /**
     * U测试帧是否发送
     */
    private volatile boolean tested = false;

    /**
     * 构造函数
     *
     * @param ctx 通道处理器上下文
     */
    public IEC104_ScheduledTaskPool(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }

    /**
     * 发送链路启动帧
     * <p>
     * 如果当前没有正在进行的启动任务，则发送STARTDT_ACT帧并启动超时检测任务
     * 超时时间为15秒，超时后将自动关闭连接
     */
    public void sendStartFrame() {

        // 通过原子引用获取当前任务
        ScheduledFuture<?> currentTask = startTask.get();

        // 防止重复发送，如果任务已完成或未提交过则跳过
        if (currentTask != null && !currentTask.isDone()) {
            return;
        }

        log.info("发送 {} 启动帧", IEC104_BasicInstructions.STARTDT_ACT);
        // 在handler中发送启动帧
        ctx.writeAndFlush(IEC104_BasicInstructions.STARTDT_ACT.retain());

        // 提交任务并开启 T1 计时器
        ScheduledFuture<?> newTask = executor.schedule(() -> {
            try {
                if (ctx.channel().isActive()) {
                    log.warn("STARTDT 超时，关闭连接");
                    ctx.close();
                }
            } catch (Exception e) {
                log.error("执行超时任务异常", e);
            }
        }, Long.parseLong(channelTimeOut.getT1()), TimeUnit.SECONDS);

        // 使用 CAS乐观锁非阻塞更新
        startTask.compareAndSet(currentTask, newTask);
    }

    /**
     * 处理接收到的启动连接确认消息
     * <p>
     * 当收到STARTDT_CON确认帧时调用此方法，用于取消超时任务并标记链路为已激活状态
     */
    public void onReceiveStartDTCon() {
        // 获取启动任务的ScheduledFuture对象
        ScheduledFuture<?> task = startTask.get();

        isCancel(task);

        // 记录日志，表示链路已激活
        log.info("收到 STARTDT_CON，链路已激活");
        // 设置启动状态为true
        started = true;
    }

    /**
     * 发送测试帧
     * <p>
     * 当经过T3时间没有报文交互时，发送TESTFR_ACT测试帧以检测链路是否正常
     * 发送后启动T1计时器等待对方确认
     */
    public void sendTestFrame() {
        // 获取t3Task的原子引用
        ScheduledFuture<?> currentTask = t3Task.get();

        // 检查是否有任务未完成，有则取消任务(重置T3)
        isCancel(currentTask);

        ScheduledFuture<?> newTask = executor.schedule(() -> {
            log.info("发送 {} 测试帧", IEC104_BasicInstructions.TESTFR_ACT);
            // 经过 T3 时间没有报文交互，发送 TESTFR_ACT U测试帧
            ctx.writeAndFlush(IEC104_BasicInstructions.TESTFR_ACT.retain());
            // 开启 T1 计时器
            startT1Timer();
        }, Long.parseLong(channelTimeOut.getT3()), TimeUnit.SECONDS);
        t3Task.compareAndSet(currentTask, newTask);
    }

    /**
     * 处理接收到的测试帧确认消息
     * <p>
     * 当收到TESTFR_CON确认帧时调用此方法，取消T1，重置T3
     */
    public void onReceiveTestFRCon() {
        isCancel(t1Task.get());
        sendTestFrame();
        // 记录日志，表示链路已激活
        log.info("收到 TESTFR_CON，链路正常");
        // 设置test发送状态为true
        tested = true;
    }

    /**
     * 启动T1定时器
     * <p>
     * 在发送 I 帧或 U 帧后启动T1定时器，用于等待对方的确认响应
     * 如果在T1时间内未收到确认，则关闭连接
     */
    private void startT1Timer() {
        ScheduledFuture<?> task = t1Task.get();
        isCancel(task);
        ScheduledFuture<?> newTask = executor.schedule(() -> {
            try {
                log.warn("T1超时，关闭连接");
                ctx.close();
            } catch (Exception e) {
                log.error("执行超时任务异常", e);
            }
        }, Long.parseLong(channelTimeOut.getT1()), TimeUnit.SECONDS);
        t1Task.compareAndSet(task, newTask);
    }

    /**
     * 取消指定的定时任务
     * <p>
     * 检查任务是否存在且未完成，如果满足条件则取消该任务
     *
     * @param task 需要取消的定时任务
     */
    private void isCancel(ScheduledFuture<?> task) {
        // 检查任务是否存在且未完成
        if (task != null && !task.isDone()) {
            // 取消超时任务
            task.cancel(false);
        }
    }

    /**
     * 安全地关闭线程池
     * <p>
     * 确保线程池停止接受新任务，并等待当前任务完成或在指定时间内终止
     * 如果在5秒内未能正常关闭，则强制关闭所有任务
     */
    public void shutdown() {
        // 检测线程池是否关闭
        if (!executor.isShutdown()) {
            // 不再接收新的任务，直到所有任务执行完毕后关闭线程池
            executor.shutdown();

            try {
                // 等待最多5秒，直到所有任务完成
                if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                    // 如果超过5秒还有未完成的任务，则尝试立即关闭所有任务
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                // 如果线程被中断，则尝试立即关闭所有任务，并重新设置线程的中断状态
                executor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * 将ScheduledTaskPool实例绑定到特定的ChannelHandlerContext
     * <p>
     * 此方法在通道初始化时创建一个ScheduledTaskPool，并将其与通道关联，以便后续使用
     *
     * @param ctx 通道处理上下文，用于操作通道的属性
     */
    public static void bindToChannel(ChannelHandlerContext ctx) {
        IEC104_ScheduledTaskPool pool = new IEC104_ScheduledTaskPool(ctx);
        ctx.channel().attr(SCHEDULED_TASK_POOL_ATTRIBUTE_KEY).set(pool);
    }

    /**
     * 从ChannelHandlerContext中获取ScheduledTaskPool实例
     * <p>
     * 此方法用于在通道的生命周期内获取之前绑定的ScheduledTaskPool实例，以便执行任务调度
     *
     * @param ctx 通道处理上下文，用于获取通道的属性
     * @return ScheduledTaskPool实例，如果没有绑定则返回null
     */
    public static IEC104_ScheduledTaskPool getFromChannel(ChannelHandlerContext ctx) {
        return ctx.channel().attr(SCHEDULED_TASK_POOL_ATTRIBUTE_KEY).get();
    }
}

