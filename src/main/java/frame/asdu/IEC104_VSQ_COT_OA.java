package frame.asdu;

import lombok.Data;
import lombok.Setter;

@Data
public class IEC104_VSQ_COT_OA {
    private final boolean SQ;
    private final short NumIx;
    private final boolean Negative;
    private final boolean Test;
    private final short CauseTx;
    private final byte SenderAddress;

    private IEC104_VSQ_COT_OA(Builder builder) {
        this.SQ = builder.SQ;
        this.NumIx = builder.NumIx;
        this.Negative = builder.Negative;
        this.Test = builder.Test;
        this.CauseTx = builder.CauseTx;
        this.SenderAddress = builder.SenderAddress;
    }

    public static class Builder {
        private final boolean SQ;
        private final short NumIx;
        private final boolean Negative;
        private final boolean Test;
        private final short CauseTx;
        private final byte SenderAddress;

        public Builder(byte VSQ, short COT, byte OA) {
            this.SQ = (VSQ << 7) == 0x80;
            this.NumIx = (short) (VSQ & ~(1 << 7));
            this.Negative = Negative;
            this.Test = Test;
            this.CauseTx = CauseTx;
            this.SenderAddress = SenderAddress;
        }

        public IEC104_VSQ_COT_OA build() {
            return new IEC104_VSQ_COT_OA(this);
        }
    }
}
