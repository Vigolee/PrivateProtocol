package handler.heart;

import config.MessageType;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import model.Header;
import model.Message;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * 心跳机制（客户端）
 * Created by Vigo on 16/4/28.
 */
public class HeartBeatReqHandler extends ChannelHandlerAdapter{

    private volatile ScheduledFuture<?> heartBeat;


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        Message message = (Message) msg;
        // 握手成功，主动发起心跳通信
        if (message.getHeader() != null && message.getHeader()
                .getType() == MessageType.LOGIN_RESPONSE) {
                heartBeat = ctx.executor().scheduleAtFixedRate(new HeartBeatTask(ctx)
                        , 0, 10000, TimeUnit.MILLISECONDS);
        }else if (message.getHeader() != null && message.getHeader()
                .getType() == MessageType.HEARTBEAT_RESPONSE) {
            System.out.println("client receive heartbeat : " + message);
        } else {
            ctx.fireChannelRead(msg);
        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (heartBeat != null) {
            heartBeat.cancel(true);
            heartBeat = null;
        }
        ctx.fireExceptionCaught(cause);
    }

    private  class HeartBeatTask implements Runnable{

        private final ChannelHandlerContext ctx;

        public HeartBeatTask(final ChannelHandlerContext ctx) {
            this.ctx = ctx;
        }

        @Override
        public void run() {
            Message heartBeatMessage = createHeartBeat();
            System.out.println("client send heartbeat: " + heartBeatMessage);
            ctx.writeAndFlush(heartBeatMessage);
        }

        private Message createHeartBeat(){
            Message message = new Message();
            Header header = new Header();
            header.setType((byte) MessageType.HEARTBEAT_REQUEST);
            message.setHeader(header);
            return message;
        }
    }
}
