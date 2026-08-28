package cloud.yunyat.model.service;

import cloud.yunyat.model.pojo.DeviceWrapper;

/**
 * 连接状态回调接口，由 UI 层（MainController）实现
 * 所有方法在调用时已保证在 UI 线程执行
 */
public interface ConnectionCallback {
    /** 状态栏消息更新 */
    void onStatusUpdate(String message);

    /** 设备运行状态变更（更新按钮状态 + 刷新表格） */
    void onDeviceStateChanged(DeviceWrapper wrapper);

    /** 弹出警告对话框 */
    void onWarning(String title, String header, String content);
}