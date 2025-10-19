//package slave.handler;
//
//import common.IEC104_BasicInstructions;
//import handler.IEC104_uFrameHandler;
//import enums.IEC104_UFrameType;
//import io.netty.buffer.ByteBuf;
//import io.netty.channel.ChannelFutureListener;
//import io.netty.channel.ChannelHandlerContext;
//import lombok.extern.log4j.Log4j2;
//
//@Log4j2
//public class IEC104_uFrameSlaveHandler extends IEC104_uFrameHandler {
//    @Override
//    public void uInstructionHandler(ChannelHandlerContext ctx, IEC104_UFrameType uFrameType) {
//        ByteBuf result = null;
//        // 根据 u帧类型判断u帧命令
//        switch (uFrameType) {
//            case STARTDT_ACT:
//                log.info("收到启动指令");
//                result = IEC104_BasicInstructions.STARTDT_CON;
//                break;
//            case STOPDT_ACT:
//                log.info("收到停止指令");
//                result = IEC104_BasicInstructions.STOPDT_CON;
//                break;
//            case TESTFR_ACT:
//                log.info("收到测试指令");
//                result = IEC104_BasicInstructions.TESTFR_CON;
//                break;
//            default:
//                log.error("U帧无效{}", uFrameType);
//                break;
//        }
//
//        // 异步发送响应字节数组
//        var future = ctx.writeAndFlush(result);
//        future.addListener((ChannelFutureListener) channelFuture -> {
//            if (channelFuture.isSuccess()) log.info("U帧发送成功");
//            else log.error("U帧发送失败:{}", String.valueOf(channelFuture.cause()));
//        });
//    }
//}
