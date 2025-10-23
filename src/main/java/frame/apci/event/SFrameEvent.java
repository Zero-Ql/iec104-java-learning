package frame.apci.event;

import lombok.Value;

@Value(staticConstructor = "of")
public class SFrameEvent {
    int recvOrdinal;
}
