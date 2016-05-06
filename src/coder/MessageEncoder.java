package coder;

import coder.marshalling.MessageMarshallingEncode;
import coder.factory.MarshallingCodeCFactory;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import model.Message;

import java.util.Iterator;
import java.util.List;

/**
 * 消息编码器
 * Created by Vigo on 16/4/21.
 */
public class MessageEncoder extends MessageToMessageEncoder<Message> {

    MessageMarshallingEncode messageMarshallingEncode;

    public MessageEncoder() {

        messageMarshallingEncode = MarshallingCodeCFactory.buildMarshallingEncoder();
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Message message, List<Object> list) throws Exception {
        if (message == null || message.getHeader() == null)
            throw new Exception("The message is null");
        ByteBuf sendBuf = Unpooled.buffer();

        sendBuf.writeInt(message.getHeader().getCrcCode());
        // 最后更新消息长度
        sendBuf.writeInt(message.getHeader().getLength());
        sendBuf.writeLong(message.getHeader().getSessionID());
        sendBuf.writeByte(message.getHeader().getType());
        sendBuf.writeByte(message.getHeader().getPriority());
        sendBuf.writeInt(message.getHeader().getAttachment().size());

        byte[] keyArray = null;
        Iterator iterator = message.getHeader().getAttachment().keySet().iterator();
        while (iterator.hasNext()){
            String key = (String) iterator.next();
            keyArray = key.getBytes("UTF-8");
            sendBuf.writeInt(keyArray.length);
            sendBuf.writeBytes(keyArray);
            Object value = message.getHeader().getAttachment().get(key);
            messageMarshallingEncode.encode(channelHandlerContext, value, sendBuf);
        }

        if (message.getBody() != null) {
            messageMarshallingEncode.encode(channelHandlerContext, message.getBody(), sendBuf);
        }

        // 在第4个字节处写入Buffer的长度
        int readableBytes = sendBuf.readableBytes();
        sendBuf.setInt(4, readableBytes);

        // 把Message添加到List传递到下一个Handler
        list.add(sendBuf);
    }
}
