package core;

import common.IEC104BasicInstructions;
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
public class ScheduledTaskPool {

    private static final Logger log = LogManager.getLogger(ScheduledTaskPool.class);

    /** 通道处理器上下文对象 */
    private final ChannelHandlerContext ctx;

    /** 保留2个线程的线程池对象 */
    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);

    /** 启动任务的原子引用 */
    private final AtomicReference<ScheduledFuture<?>> startTask = new AtomicReference<>();

    /** ScheduledTaskPool在Channel属性中的键值 */
    private static final AttributeKey<ScheduledTaskPool> SCHEDULED_TASK_POOL_ATTRIBUTE_KEY =
        AttributeKey.valueOf("scheduledTaskPool");

    /** 链路是否已启动标志 */
    private volatile boolean started = false;

    /**
     * 构造函数
     *
     * @param ctx 通道处理器上下文
     */
    public ScheduledTaskPool(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }

    /**
     * 发送链路启动帧
     * <p>
     * 如果当前没有正在进行的启动任务，则发送STARTDT_ACT帧并启动超时检测任务
     * 超时时间为5秒，超时后将自动关闭连接
     */
    public void sendStartFrame() {
        // 通过原子引用获取当前任务
        ScheduledFuture<?> currentTask = startTask.get();

        // 防止重复发送，如果任务已完成或未提交过则跳过
        if (currentTask != null && !currentTask.isDone()) {
            return;
        }

        log.info("发送 {} 启动帧", IEC104BasicInstructions.STARTDT_ACT);
        // 在handler中发送启动帧
        ctx.writeAndFlush(IEC104BasicInstructions.STARTDT_ACT);

        // 提交任务并开启延时5秒的计时器
        ScheduledFuture<?> newTask = executor.schedule(() -> {
            try {
                if (ctx.channel().isActive()) {
                    log.warn("STARTDT 超时，关闭连接");
                    ctx.close();
                }
            } catch (Exception e) {
                log.error("执行超时任务异常", e);
            }
        }, 5, TimeUnit.SECONDS);

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

        // 检查任务是否存在且未完成
        if (task != null && !task.isDone()) {
            // 取消超时任务
            task.cancel(false);
        }

        // 记录日志，表示链路已激活
        log.info("收到 STARTDT_CON，链路已激活");
        // 设置启动状态为true
        started = true;
    }

    public void sendTestFrame(){
        ScheduledFuture<?> currentTask = startTask.get();

        int timetest = 15;

        // 防止重复发送，如果任务已完成或未提交过则跳过
        if (currentTask != null && !currentTask.isDone()) {
            return;
        }

        ScheduledFuture<?> newTask = executor.scheduleWithFixedDelay(() -> {
            if (){

            }
        }, timetest, timetest, TimeUnit.SECONDS);

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
        ScheduledTaskPool pool = new ScheduledTaskPool(ctx);
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
    public static ScheduledTaskPool getFromChannel(ChannelHandlerContext ctx) {
        return ctx.channel().attr(SCHEDULED_TASK_POOL_ATTRIBUTE_KEY).get();
    }
}

