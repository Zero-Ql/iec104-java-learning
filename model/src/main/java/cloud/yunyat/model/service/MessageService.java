package cloud.yunyat.model.service;

import cloud.yunyat.model.impl.iec104.frame.asdu.IEC104_AsduMessageDetail;

import java.util.function.Consumer;

public interface MessageService {
    /**
     * 当收到或发送报文时触发
     * @param stationId 站点标识
     * @param message 解析后的报文对象
     */
    void getMessage(String stationId, Consumer<IEC104_AsduMessageDetail> message);
}
