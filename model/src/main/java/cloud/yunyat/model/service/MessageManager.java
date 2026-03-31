package cloud.yunyat.model.service;

import cloud.yunyat.model.pojo.AnalogInput;
import cloud.yunyat.model.pojo.StatusInput;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public class MessageManager implements MessageService {

    private static final MessageManager INSTANCE = new MessageManager();

    public static MessageManager getInstance() {
        return INSTANCE;
    }

    /**
     * 存储遥测订阅者
     * key: 站点标识
     * value: 订阅者列表
     */
    private final Map<Integer, List<Consumer<AnalogInput>>> ycListener = new ConcurrentHashMap<>();
    private final Map<Integer, List<Consumer<StatusInput>>> yxListener = new ConcurrentHashMap<>();

    @Override
    public void subscribeYcData(int stationId, Consumer<AnalogInput> consumer) {
        // 注册订阅者
        ycListener.computeIfAbsent(stationId, k -> new CopyOnWriteArrayList<>()).add(consumer);
    }

    @Override
    public void subscribeYxData(int stationId, Consumer<StatusInput> consumer) {
        // 注册订阅者
        yxListener.computeIfAbsent(stationId, k -> new CopyOnWriteArrayList<>()).add(consumer);
    }

    /**
     * 获取订阅者列表，如果失败则返回空的列表
     * 遍历列表并通知订阅者
     *
     * @param stationId 站点标识
     * @param detail    消息详情
     */
    public void publishYcData(int stationId, AnalogInput detail) {
        ycListener.getOrDefault(stationId, List.of())
                .forEach(con -> con.accept(detail));
    }

    public void publishYxData(int stationId, StatusInput detail) {
        yxListener.getOrDefault(stationId, List.of())
                .forEach(con -> con.accept(detail));
    }
}
