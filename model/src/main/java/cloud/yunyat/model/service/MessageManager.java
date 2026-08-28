package cloud.yunyat.model.service;

import cloud.yunyat.model.pojo.AnalogInput;
import cloud.yunyat.model.pojo.Coa;
import cloud.yunyat.model.pojo.StatusInput;
import lombok.extern.log4j.Log4j2;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

@Log4j2
public class MessageManager implements MessageService{

    /**
     * 存储遥测订阅者
     * key: 站点标识
     * value: 订阅者列表
     */
    private final Map<Coa, List<Consumer<AnalogInput>>> ycListener = new ConcurrentHashMap<>();
    private final Map<Coa, List<Consumer<StatusInput>>> yxListener = new ConcurrentHashMap<>();

    @Override
    public Runnable subscribeYcData(Coa stationId, Consumer<AnalogInput> consumer) {
        // 注册订阅者
        ycListener.computeIfAbsent(stationId, k -> new CopyOnWriteArrayList<>()).add(consumer);
        return () -> {
            List<Consumer<AnalogInput>> list = ycListener.get(stationId);
            if (list != null) {
                list.remove(consumer);
                if (list.isEmpty()) ycListener.remove(stationId);  // 清理空 key
            }
        };
    }

    @Override
    public Runnable subscribeYxData(Coa stationId, Consumer<StatusInput> consumer) {
        // 注册订阅者
        yxListener.computeIfAbsent(stationId, k -> new CopyOnWriteArrayList<>()).add(consumer);
        return () -> {
            List<Consumer<StatusInput>> list = yxListener.get(stationId);
            if (list != null) {
                list.remove(consumer);
                if (list.isEmpty()) yxListener.remove(stationId);  // 清理空 key
            }
        };
    }

    /**
     * 获取订阅者列表，如果失败则返回空的列表
     * 遍历列表并通知订阅者
     *
     * @param stationId 站点标识
     * @param detail    消息详情
     */
    public void publishYcData(Coa stationId, AnalogInput detail) {
        ycListener.getOrDefault(stationId, List.of())
                .forEach(con -> con.accept(detail));
    }

    public void publishYxData(Coa stationId, StatusInput detail) {
        yxListener.getOrDefault(stationId, List.of())
                .forEach(con -> con.accept(detail));
    }
}
