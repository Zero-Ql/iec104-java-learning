package cloud.yunyat.model.service;

import cloud.yunyat.model.master.IEC104_Client;
import cloud.yunyat.model.pojo.*;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * 设备连接管理器，负责 IEC104 客户端的生命周期管理
 * 通过 ConnectionCallback 和 UiThreadRunner 解耦 UI 层依赖
 */
@Log4j2
public class ClientConnectionManager {

    /** 活跃客户端映射：deviceName → IEC104_Client */
    private final Map<String, IEC104_Client> activeClients = new ConcurrentHashMap<>();

    /** UI 回调接口 */
    private final ConnectionCallback callback;

    /** UI 线程执行器（由上层注入，如 Platform::runLater） */
    private final Consumer<Runnable> uiThreadRunner;

    public ClientConnectionManager(ConnectionCallback callback, Consumer<Runnable> uiThreadRunner) {
        this.callback = callback;
        this.uiThreadRunner = uiThreadRunner;
    }

    /**
     * 检查指定设备是否正在运行
     */
    public boolean isRunning(String deviceName) {
        return activeClients.containsKey(deviceName);
    }

    /**
     * 启动设备连接
     */
    public void startDevice(DeviceWrapper wrapper) {
        String deviceName = wrapper.getDisplayName();
        Device device = wrapper.getDevice();

        // 收集 RTU 的 COA 列表
        List<Short> rtuCoasList = new ArrayList<>();
        for (Rtu rtu : device.getRtuList()) {
            rtuCoasList.add((short) rtu.getCOA().value());
        }

        if (rtuCoasList.isEmpty()) {
            callback.onWarning("警告", "无法启动设备", "该设备未配置 RTU");
            return;
        }

        callback.onStatusUpdate("设备 " + deviceName + " 连接中...");

        Thread clientThread = new Thread(() -> {
            IEC104_Client client = new IEC104_Client(device.getIp(), device.getPort(), rtuCoasList);

            // 为每个 RTU 绑定数据监听器
            for (Rtu rtu : device.getRtuList()) {
                bindRtuDataListeners(rtu, client);
            }

            activeClients.put(deviceName, client);
            device.setDeviceStatusColumn(true);
            uiThreadRunner.accept(() -> callback.onDeviceStateChanged(wrapper));

            try {
                uiThreadRunner.accept(() -> callback.onStatusUpdate("设备 " + deviceName + " 运行中"));
                client.run();
                uiThreadRunner.accept(() -> {
                    log.info("设备 {} 已断开连接", deviceName);
                    callback.onStatusUpdate("设备 " + deviceName + " 已断开");
                });
            } catch (InterruptedException e) {
                log.info("设备 {} 已被用户手动停止", deviceName);
                uiThreadRunner.accept(() -> callback.onStatusUpdate("设备 " + deviceName + " 已停止"));
            } catch (Exception e) {
                log.error("设备 {} 启动或运行异常: ", deviceName, e);
                uiThreadRunner.accept(() -> {
                    callback.onWarning("警告", "启动设备失败",
                            "设备 '" + deviceName + "' 启动失败：\n" + e.getMessage());
                    callback.onStatusUpdate("设备 " + deviceName + " 启动失败");
                });
            } finally {
                activeClients.remove(deviceName);
                device.setDeviceStatusColumn(false);
                uiThreadRunner.accept(() -> callback.onDeviceStateChanged(wrapper));
            }
        });

        clientThread.setDaemon(true);
        clientThread.setName("IEC104-ClientThread-" + deviceName);
        clientThread.start();
    }

    /**
     * 停止设备连接
     */
    public void stopDevice(DeviceWrapper wrapper) {
        String deviceName = wrapper.getDisplayName();
        IEC104_Client client = activeClients.get(deviceName);

        if (client != null) {
            log.info("正在手动停止设备: {}", deviceName);
            client.stop();
            log.info("设备 {} 已发出停止指令", deviceName);
        } else {
            wrapper.getDevice().setDeviceStatusColumn(false);
            uiThreadRunner.accept(() -> callback.onDeviceStateChanged(wrapper));
            callback.onWarning("警告", "无法停止设备", "该设备目前未在运行状态");
        }
    }

    /**
     * 为 RTU 绑定遥测/遥信数据监听器
     */
    private void bindRtuDataListeners(Rtu rtu, IEC104_Client client) {
        client.getMessageManager().subscribeYcData(rtu.getCOA(), yc ->
                uiThreadRunner.accept(() -> rtu.updateOrAddYc(yc))
        );

        client.getMessageManager().subscribeYxData(rtu.getCOA(), yx ->
                uiThreadRunner.accept(() -> rtu.updateOrAddYx(yx))
        );
    }
}