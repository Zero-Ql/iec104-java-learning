package cloud.yunyat.model.pojo;

import cloud.yunyat.model.impl.iec104.enums.IEC104_TypeIdentifier;
import javafx.beans.property.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Builder(builderMethodName = "newYcData")
@AllArgsConstructor
@NoArgsConstructor
public class AnalogInput {
    private StringProperty name = new SimpleStringProperty();
    private ObjectProperty<IEC104_TypeIdentifier> typeIdentifier = new SimpleObjectProperty<>();
    private IntegerProperty point = new SimpleIntegerProperty();
    private DoubleProperty value = new SimpleDoubleProperty();
    private IntegerProperty quality = new SimpleIntegerProperty();
    private ObjectProperty<LocalDateTime> time = new SimpleObjectProperty<>();
    private DoubleProperty max = new SimpleDoubleProperty();
    private DoubleProperty min = new SimpleDoubleProperty();
    private DoubleProperty coefficient = new SimpleDoubleProperty();
    @Setter
    @Getter
    private Map<String, Boolean> map = new HashMap<>();


    /**
     * @param name 遥测点名称
     * @param typeIdentifier 类型标识符
     * @param point 点号
     * @param value 值
     * @param quality 质量码
     * @param max 最大值
     * @param min 最小值
     * @param coefficient 系数值
     */
    public AnalogInput(String name, IEC104_TypeIdentifier typeIdentifier, int point, double value, int quality, double max, double min, double coefficient) {
        this.name.set(name);
        this.typeIdentifier.set(typeIdentifier);
        this.point.set(point);
        this.value.set(value);
        this.quality.set(quality);
        this.time.set(LocalDateTime.now());
        this.max.set(max);
        this.min.set(min);
        this.coefficient.set(coefficient);
    }

    public AnalogInput(IEC104_TypeIdentifier typeIdentifier, int point, double value, int quality, Map<String, Boolean> qualityBits){
        // 自动生成一个占位名称，例如 "未命名_1"
        this.name.set("未命名_" + point);
        this.typeIdentifier.set(typeIdentifier);
        this.point.set(point);
        this.value.set(value);
        this.quality.set(quality);
        this.time.set(LocalDateTime.now());

        // 给系数、最大最小值赋默认值
        if (value > this.max.get()) this.max.set(value);
        if (value < this.min.get()) this.min.set(value);
        this.coefficient.set(1.0);

        this.map.putAll(qualityBits);
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public IEC104_TypeIdentifier getTypeIdentifier() {
        return typeIdentifier.get();
    }

    public ObjectProperty<IEC104_TypeIdentifier> typeIdentifierProperty() {
        return typeIdentifier;
    }

    public void setTypeIdentifier(IEC104_TypeIdentifier typeIdentifier) {
        this.typeIdentifier.set(typeIdentifier);
    }

    public int getPoint() {
        return point.get();
    }

    public IntegerProperty pointProperty() {
        return point;
    }

    public void setPoint(int point) {
        this.point.set(point);
    }

    public double getValue() {
        return value.get();
    }

    public DoubleProperty valueProperty() {
        return value;
    }

    public void setValue(double value) {
        this.value.set(value);
    }

    public int getQuality() {
        return quality.get();
    }

    public IntegerProperty qualityProperty() {
        return quality;
    }

    public void setQuality(int quality) {
        this.quality.set(quality);
    }

    public LocalDateTime getTime() {
        return time.get();
    }

    public ObjectProperty<LocalDateTime> timeProperty() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time.set(time);
    }

    public double getMax() {
        return max.get();
    }

    public DoubleProperty maxProperty() {
        return max;
    }

    public void setMax(double max) {
        this.max.set(max);
    }

    public double getMin() {
        return min.get();
    }

    public DoubleProperty minProperty() {
        return min;
    }

    public void setMin(double min) {
        this.min.set(min);
    }

    public double getCoefficient() {
        return coefficient.get();
    }

    public DoubleProperty coefficientProperty() {
        return coefficient;
    }

    public void setCoefficient(double coefficient) {
        this.coefficient.set(coefficient);
    }
}
