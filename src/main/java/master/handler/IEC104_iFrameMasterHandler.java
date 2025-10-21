package master.handler;

import frame.asdu.IEC104_AsduMessageDetail;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class IEC104_iFrameMasterHandler extends SimpleChannelInboundHandler<IEC104_AsduMessageDetail> {

    /**
     * @param ctx 通道上下文
     * @param asdu asdu对象
     * @throws Exception
     */
    @Override
    protected void messageReceived(ChannelHandlerContext ctx, IEC104_AsduMessageDetail asdu) throws Exception {
        if(asdu instanceof IEC104_AsduMessageDetail){
            asdu.toString();
        }
    }

    public String toString() {
        return """
                类型标识：%s
                可变结构限定词：
                """.formatted();
    }
}
