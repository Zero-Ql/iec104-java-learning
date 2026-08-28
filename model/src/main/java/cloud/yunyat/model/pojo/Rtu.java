package cloud.yunyat.model.pojo;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Data
@Log4j2
@Builder(builderMethodName = "newRtu")
@AllArgsConstructor
@NoArgsConstructor
public class Rtu {
    private String name;
    private Coa COA;
    private boolean enable;

    /** TableView 绑定的 ObservableList（保持 UI 兼容） */
    @Builder.Default
    private ObservableList<AnalogInput> ycList = FXCollections.observableArrayList();
    @Builder.Default
    private ObservableList<StatusInput> yxList = FXCollections.observableArrayList();

    /** O(1) 点号索引，与 ycList/yxList 保持同步 */
    @Builder.Default
    private final Map<Integer, AnalogInput> ycMap = new ConcurrentHashMap<>();
    @Builder.Default
    private final Map<Integer, StatusInput> yxMap = new ConcurrentHashMap<>();

    /** 单个 RTU 最大测点数（防止无限追加导致 OOM） */
    private static final int MAX_YC_POINTS = 4096;
    private static final int MAX_YX_POINTS = 4096;

    /**
     * 更新或追加遥测数据（O(1) + 检查最大点数）
     * @param yc 收到的遥测数据
     */
    public void updateOrAddYc(AnalogInput yc) {
        int point = yc.pointProperty().get();
        AnalogInput existing = ycMap.get(point);
        if (existing != null) {
            existing.valueProperty().set(yc.valueProperty().get());
            existing.qualityProperty().set(yc.qualityProperty().get());
            existing.timeProperty().set(yc.timeProperty().get());
            if (yc.valueProperty().get() > existing.getMax()) existing.setMax(yc.valueProperty().get());
            if (yc.valueProperty().get() < existing.getMin()) existing.setMin(yc.valueProperty().get());
        } else if (ycList.size() < MAX_YC_POINTS) {
            ycMap.put(point, yc);
            ycList.add(yc);
        }else {
            log.error("RTU[{}] 遥测点数已达上限 {}，拒绝新点号: {}", COA, MAX_YC_POINTS, point);
        }
    }

    /**
     * 更新或追加遥信数据（O(1) + 检查最大点数）
     * @param yx 收到的遥信数据
     */
    public void updateOrAddYx(StatusInput yx) {
        int point = yx.pointProperty().get();
        StatusInput existing = yxMap.get(point);
        if (existing != null) {
            existing.valueProperty().set(yx.valueProperty().get());
            existing.qualityProperty().set(yx.qualityProperty().get());
            existing.timeProperty().set(yx.timeProperty().get());
        } else if (yxList.size() < MAX_YX_POINTS) {
            yxMap.put(point, yx);
            yxList.add(yx);
        }else {
            log.error("RTU[{}] 遥信点数已达上限 {}，拒绝新点号: {}", COA, MAX_YX_POINTS, point);
        }
    }
}