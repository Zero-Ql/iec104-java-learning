package cloud.yunyat.model.pojo;

/**
 * IEC104 公共地址（Common Object Address）值类型
 */
public record Coa(int value) {
    public Coa{
        if (value < 0 || value > 65535) {
            throw new IllegalArgumentException("COA 超出范围 [0, 65535]: " + value);
        }
    }
}
