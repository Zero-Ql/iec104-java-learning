package frame.apci.event;


import lombok.Value;

@Value(staticConstructor = "of")
public class UFrameEvent {
    byte control;
    boolean test;
    boolean stop;
    boolean start;
    boolean test_con;
    boolean start_con;
    boolean stop_con;
}
