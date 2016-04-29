package handler.heart;

import config.MessageType;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import model.Header;
import model.Message;

/**
 * Created by Vigo on 16/4/28.
 */
public class HeartBeatRespHandler extends ChannelHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Message message = (Message) msg;

        //返回心跳应答消息
        if (message.getHeader() != null && message.getHeader()
                .getType() == MessageType.HEARTBEAT_REQUEST) {
            System.out.println("server receive heartBeat : " + message);
            Message heartBeatMessage = createHeartBeat();
            ctx.writeAndFlush(heartBeatMessage);
        }else {
            ctx.fireChannelRead(msg);
        }
    }

    private Message createHeartBeat(){
        Message message = new Message();
        Header header = new Header();
        header.setType((byte) MessageType.HEARTBEAT_RESPONSE);
        message.setHeader(header);
        return message;
    }
}
