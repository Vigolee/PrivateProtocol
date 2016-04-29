package coder.marshalling;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.marshalling.MarshallingDecoder;
import io.netty.handler.codec.marshalling.UnmarshallerProvider;

/**
 * Created by Vigo on 16/4/26.
 */
public class MessageMarshallingDecode extends MarshallingDecoder{

    public MessageMarshallingDecode(UnmarshallerProvider provider, int maxObjectSize){
        super(provider, maxObjectSize);
    }

    public Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        return super.decode(ctx, in);
    }
}
