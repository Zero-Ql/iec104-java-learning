package cloud.yunyat.model.pojo;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder(builderMethodName = "newDevice")
@AllArgsConstructor
@NoArgsConstructor
public class Device {
    private String name;
    private String ip;
    private int port;
    private int t1;           // 超时时间T1
    private int t2;           // 超时时间T2
    private int t3;           // 超时时间T3
    private int w;            // 发送窗口大小
    private int generalInterrogation;  // 通用询问周期
    private int clockSynchronization;  // 时钟同步周期
    private String gmt;       // 时区设置
    private boolean enableTimezone; // 是否启用时区
    private boolean deviceStatusColumn; // 设备状态

    @Builder.Default
    private List<Rtu> rtuList=new ArrayList<>();
}
