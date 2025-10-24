package frame;


import frame.apci.IEC104_ApciMessageDetail;
import frame.asdu.IEC104_AsduMessageDetail;
import lombok.Data;

/**
 * 拼接完整帧
 */
@Data
public class IEC104_FrameBuilder {
    private final IEC104_ApciMessageDetail apciMessageDetail;
    private IEC104_AsduMessageDetail asduMessageDetail;

    private IEC104_FrameBuilder(Builder builder) {
        this.apciMessageDetail = builder.apciMessageDetail;
        this.asduMessageDetail = builder.asduMessageDetail;
    }

    public static class Builder {
        private final IEC104_ApciMessageDetail apciMessageDetail;
        private IEC104_AsduMessageDetail asduMessageDetail = null;

        public Builder(IEC104_ApciMessageDetail apciMessageDetail) {
            this.apciMessageDetail = apciMessageDetail;
        }

        public Builder setAsduMessageDetail(IEC104_AsduMessageDetail asduMessageDetail) {
            this.asduMessageDetail = asduMessageDetail;
            return this;
        }

        public IEC104_FrameBuilder build() {
            return new IEC104_FrameBuilder(this);
        }
    }
}