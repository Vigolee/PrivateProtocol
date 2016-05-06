package handler.auth;

import config.MessageType;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import model.Header;
import model.Message;

/** Login业务处理类(客户端)
 * Created by Vigo on 16/4/26.
 */
public class LoginAuthReqHandler extends ChannelHandlerAdapter {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("client send login message : " + createLoginRequest());
        ctx.writeAndFlush(createLoginRequest());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Message message = (Message) msg;

        if (message.getHeader() != null && message.getHeader()
                .getType() == MessageType.LOGIN_RESPONSE){
                System.out.println("Login is ok, message is : " + message);
            }
        ctx.fireChannelRead(msg);
    }

    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }
    private Message createLoginRequest(){
        Message message = new Message();
        Header header = new Header();
        header.setType((byte) MessageType.LOGIN_REQUEST);
        message.setHeader(header);
        return message;
    }

}
