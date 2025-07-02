package enums;

public enum IEC104State {
    /**
     * 断开
     */
    DISCONNECTED,
    /**
     * 正在连接
     */
    CONNECTING,
    /**
     * 等待启动链路帧确认
     */
    WAIT_STARTED_CON,
    /**
     * 链路已建立
     */
    LINK_ESTABLISHED,
    /**
     * 正在总召
     */
    GENERAL_CALL,
    /**
     * 数据交互中
     */
    DATA_EXCHANGE,
    /**
     * 仅维持心跳
     */
    HEARTBEAT_ONLY,
    /**
     * 正在重连
     */
    RECONNECTING
}
