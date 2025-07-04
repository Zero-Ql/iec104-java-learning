package IEC104Frameformat;

public class MessageInfo {
    /**
     * IOA 3字节 信息对象地址
     * 一般情况下仅前两个字节可用，不同对象地址的最大数量限制为 65535
     * 第三个字节用于构造信息对象地址
     */
    private int messageAddress;

    /**
     * NVA 2字节 标准化值
     */
    private byte [] nva ;


}
