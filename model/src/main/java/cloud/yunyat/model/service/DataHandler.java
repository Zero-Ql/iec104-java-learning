package cloud.yunyat.model.service;

public interface DataHandler {
    // 遥测更新
    void onAnalogUpdate(int address, double value);
    // 遥信更新
    void onDigitalUpdate(int address, boolean state);
}
