package coder;

import coder.factory.MarshallingCodeCFactory;
import coder.marshalling.MessageMarshallingDecode;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import model.Header;
import model.Message;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Vigo on 16/4/26.
 */
public class MessageDecoder extends LengthFieldBasedFrameDecoder {

    MessageMarshallingDecode messageMarshallingDecode;

    public MessageDecoder(int maxFrameLength, int lengthFieldOffset,
                          int lengthFieldLength,int lengthAdjustment, int initialBytesToStrip) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip);
        messageMarshallingDecode = MarshallingCodeCFactory.buildMarshallingDecoder();
    }

    @Override
    public Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        System.out.println("------");
        ByteBuf byteBuf = (ByteBuf) super.decode(ctx, in);

        if (byteBuf == null){
            System.out.println("byteBuf is null");
            return null;
        }
        Message message = new Message();
        Header header = new Header();
        header.setCrcCode(byteBuf.readInt());
        header.setLength(byteBuf.readInt());
        header.setSessionID(byteBuf.readLong());
        header.setType(byteBuf.readByte());
        header.setPriority(byteBuf.readByte());

        int size = byteBuf.readInt();
        if (size > 0){
            Map<String, Object> attch = new HashMap<String, Object>(size);
            byte[] keyArray = null;
            for (int i = 0; i < size; ++i) {
                int keySize = byteBuf.readInt();
                keyArray = new byte[keySize];
                byteBuf.readBytes(keyArray);
                String key = new String(keyArray, "UTF-8");
                attch.put(key, messageMarshallingDecode.decode(ctx, byteBuf));
            }
            header.setAttachment(attch);
        }

        if (in.readableBytes() > 4) {
            message.setBody(messageMarshallingDecode.decode(ctx, byteBuf));
        }
        message.setHeader(header);

        return message;
    }
}
