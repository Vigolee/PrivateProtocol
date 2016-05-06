package handler.auth;

import config.Configuration;
import config.MessageType;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import model.Header;
import model.Message;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Login业务处理
 * Created by Vigo on 16/4/28.
 */
public class LoginAuthRespHandler extends ChannelHandlerAdapter {

    private Map<String, Boolean> nodes = new ConcurrentHashMap<String, Boolean>();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Message message = (Message) msg;

        //握手请求消息处理，其他消息传递
        if (message.getHeader() != null && message.getHeader().
                getType() == MessageType.LOGIN_REQUEST) {
            String node = ctx.channel().remoteAddress().toString();
            Message response = null;
            //重复登陆，拒绝
            if (nodes.containsKey(node)){
                response = createLoginResponse((byte) -1);
            } else {
                InetSocketAddress address = (InetSocketAddress) ctx.channel().remoteAddress();
                String ip = address.getAddress().getHostAddress();
                boolean isOk = false;
                for (int i = 0; i < Configuration.whiteList.length; i++) {
                    if (ip.equals(Configuration.whiteList[i])){
                        isOk = true;
                        break;
                    }
                }
                if (isOk) {
                    response = createLoginResponse((byte) 0);
                    nodes.put(ip, true);
                } else {
                    response = createLoginResponse((byte) -1);
                }
                System.out.println("Login response is : " + response);
                ctx.writeAndFlush(response);
            }
        } else {
            ctx.fireChannelRead(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        nodes.remove(ctx.channel().remoteAddress().toString());//从登陆表中删除
        ctx.close();
        ctx.fireExceptionCaught(cause);
    }

    private Message createLoginResponse(byte result){
        Message message = new Message();
        Header header = new Header();
        header.setType((byte) MessageType.LOGIN_RESPONSE);
        message.setHeader(header);
        message.setBody(result);
        return message;
    }
}
