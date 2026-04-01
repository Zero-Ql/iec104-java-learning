package cloud.yunyat.model.service;

import cloud.yunyat.model.pojo.AnalogInput;
import cloud.yunyat.model.pojo.StatusInput;

import java.util.function.Consumer;

/**
 * 当收到或发送报文时触发
 */
public interface MessageService {

    // 订阅遥测数据更新
    default void subscribeYcData(int stationId, Consumer<AnalogInput> ycDetail){}

    // 订阅遥信数据更新
    default void subscribeYxData(int stationId, Consumer<StatusInput> yxDetail){}
}
