package cloud.yunyat.model.service;

import cloud.yunyat.model.impl.iec104.frame.asdu.IEC104_AsduMessageDetail;

import java.util.function.Consumer;

public class MessageManager implements MessageService{
    @Override
    public void getMessage(String stationId, Consumer<IEC104_AsduMessageDetail> message) {

    }
}
