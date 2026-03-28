package cloud.yunyat.model.pojo;

import cloud.yunyat.model.impl.iec104.enums.IEC104_TypeIdentifier;
import javafx.beans.property.*;

import java.time.LocalDateTime;

public class AnalogInput {
    private final StringProperty name = new SimpleStringProperty();
    private final ObjectProperty<IEC104_TypeIdentifier> typeIdentifier = new SimpleObjectProperty<>();
    private final IntegerProperty point = new SimpleIntegerProperty();
    private final DoubleProperty value = new SimpleDoubleProperty();
    private final IntegerProperty quality = new SimpleIntegerProperty();
    private final ObjectProperty<LocalDateTime> time = new SimpleObjectProperty<>();
    private final DoubleProperty max = new SimpleDoubleProperty();
    private final DoubleProperty min = new SimpleDoubleProperty();
    private final DoubleProperty coefficient = new SimpleDoubleProperty();


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
    AnalogInput(String name, IEC104_TypeIdentifier typeIdentifier, int point, double value, int quality, double max, double min, double coefficient) {
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

    public StringProperty nameProperty() {
        return name;
    }

    public ObjectProperty<IEC104_TypeIdentifier> typeIdentifierProperty() {
        return typeIdentifier;
    }

    public IntegerProperty pointProperty() {

        return point;
    }

    public DoubleProperty valueProperty() {
        return value;
    }

    public IntegerProperty qualityProperty() {
        return quality;
    }

    public ObjectProperty<LocalDateTime> timeProperty() {
        return time;
    }

    public DoubleProperty maxProperty() {
        return max;
    }

    public DoubleProperty minProperty() {
        return min;
    }

    public DoubleProperty coefficientProperty() {
        return coefficient;
    }
}
