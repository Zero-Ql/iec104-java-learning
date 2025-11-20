/*
 * IEC 60870-5-104 Protocol Implementation
 * Copyright (C) 2025 QSky
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */
package impl.iec104.core.scheduler;

import impl.iec104.common.IEC104_IFrameTaskManager;
import impl.iec104.common.IEC104_SFrameTaskManager;
import impl.iec104.common.IEC104_TimeOutTaskManager;
import impl.iec104.common.IEC104_UFrameTaskManager;
import impl.iec104.config.Piec104Config;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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
     * 保留2个线程的线程池对象
     */
    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);

    /**
     * u帧任务管理器
     */
    private final IEC104_UFrameTaskManager uFrameTaskManager;

    /**
     * s帧任务管理器
     */
    private final IEC104_SFrameTaskManager sFrameTaskManager;

    /**
     * i帧任务管理器
     */
    private final IEC104_IFrameTaskManager iFrameTaskManager;

    /**
     * 超时任务管理器
     */
    private final IEC104_TimeOutTaskManager timeOutTaskManager;


    /**
     * 获取通道配置实例
     */
    private static final Piec104Config piec104Config = Piec104Config.getInstance();

    /**
     * ScheduledTaskPool在Channel属性中的键值
     */
    private static final AttributeKey<IEC104_ScheduledTaskPool> SCHEDULED_TASK_POOL_ATTRIBUTE_KEY = AttributeKey.valueOf("scheduledTaskPool");

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
        this.uFrameTaskManager = new IEC104_UFrameTaskManager(this, ctx, executor, piec104Config);
        this.timeOutTaskManager = new IEC104_TimeOutTaskManager(this, ctx, executor, piec104Config);
        this.sFrameTaskManager = new IEC104_SFrameTaskManager(ctx, executor, piec104Config);
        this.iFrameTaskManager = new IEC104_IFrameTaskManager(this, ctx);
    }


    public void sendStartFrame() {
        uFrameTaskManager.sendStartFrame();
    }

    /**
     * 处理接收到的启动连接确认消息
     * <p>
     * 当收到STARTDT_CON确认帧时调用此方法，用于取消超时任务并标记链路为已激活状态
     */
    public void onReceiveStartDTCon() {
        uFrameTaskManager.onReceiveStartDTCon();

        // 记录日志，表示链路已激活
        log.info("收到 STARTDT_CON，链路已激活");
        // 设置启动状态为true
        started = true;
    }

    public void sendSFrame(short recvOrdinal) {
        sFrameTaskManager.sendSFrame(recvOrdinal);
    }

    /**
     * 发送测试帧
     * <p>
     * 当经过T3时间没有报文交互时，发送TESTFR_ACT测试帧以检测链路是否正常
     * 发送后启动T1计时器等待对方确认
     */
    public void sendTestFrame() {
        uFrameTaskManager.sendTestFrame();
    }

    /**
     * 发送总召
     * <p>
     * 当收到启动帧回复后立即调用此方法
     * 发送后启动T1计时器等待对方确认
     */
    public void sendInterrogationCommand() {
        iFrameTaskManager.sendInterrogationCommand();
    }


    /**
     * 处理接收到的测试帧确认消息
     * <p>
     * 当收到TESTFR_CON确认帧时调用此方法，取消T1，重置T3
     */
    public void onReceiveTestFRCon() {
        // 取消T1
        onReceiveT1Timer();
        // 取消T3
        uFrameTaskManager.onReceiveTestFRCon();
        // 设置test发送状态为true
        tested = true;
        // 重启T3
        sendTestFrame();
    }

    /**
     * 启动T1定时器
     * <p>
     * 在发送 I 帧或 U 帧后启动T1定时器，用于等待对方的确认响应
     * 如果在T1时间内未收到确认，则关闭连接
     */
    public void startT1Timer() {
        timeOutTaskManager.startT1Timer();
    }

    public void onReceiveT1Timer() {
        timeOutTaskManager.cancelT1Timer();
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

