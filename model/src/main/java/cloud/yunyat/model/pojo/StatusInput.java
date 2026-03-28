package cloud.yunyat.model.pojo;

import cloud.yunyat.model.impl.iec104.enums.IEC104_TypeIdentifier;
import javafx.beans.property.*;

import java.time.LocalDateTime;

public class StatusInput {
    private final StringProperty name = new SimpleStringProperty();
    private final ObjectProperty<IEC104_TypeIdentifier> typeIdentifier = new SimpleObjectProperty<>();
    private final IntegerProperty point = new SimpleIntegerProperty();
    private final BooleanProperty value = new SimpleBooleanProperty();
    private final IntegerProperty quality = new SimpleIntegerProperty();
    private final ObjectProperty<LocalDateTime> time = new SimpleObjectProperty<>();
    private final BooleanProperty invert = new SimpleBooleanProperty();

    /**
     * @param name           遥信点名称
     * @param typeIdentifier 遥信点类型
     * @param point          遥信点地址
     * @param value          遥信点值
     * @param quality        质量码
     * @param invert         是否反转
     */
    StatusInput(String name, IEC104_TypeIdentifier typeIdentifier, int point, boolean value, int quality, boolean invert) {
        this.name.set(name);
        this.typeIdentifier.set(typeIdentifier);
        this.point.set(point);
        this.value.set(value);
        this.quality.set(quality);
        this.invert.set(invert);
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

    public BooleanProperty valueProperty() {
        return value;
    }

    public IntegerProperty qualityProperty() {
        return quality;
    }

    public ObjectProperty<LocalDateTime> timeProperty() {
        return time;
    }

    public BooleanProperty invertProperty() {
        return invert;
    }
}
