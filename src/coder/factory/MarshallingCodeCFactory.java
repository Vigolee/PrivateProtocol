package coder.factory;

import coder.marshalling.MessageMarshallingDecode;
import coder.marshalling.MessageMarshallingEncode;
import io.netty.handler.codec.marshalling.*;
import org.jboss.marshalling.MarshallerFactory;
import org.jboss.marshalling.Marshalling;
import org.jboss.marshalling.MarshallingConfiguration;

/**
 *
 * Created by Vigo on 16/4/26.
 */
public class MarshallingCodeCFactory {

    /**
     * 创建Marshalling编码器
     * @return
     */
    public static MessageMarshallingEncode buildMarshallingEncoder(){
        final MarshallerFactory marshallerFactory = Marshalling.
                getProvidedMarshallerFactory("serial");// java序列化对象
        final MarshallingConfiguration configuration = new MarshallingConfiguration();
        configuration.setVersion(5);
        MarshallerProvider provider = new DefaultMarshallerProvider(marshallerFactory, configuration);
        MessageMarshallingEncode encoder = new MessageMarshallingEncode(provider);
        return encoder;

    }

    /**
     * 创建Marshalling解码器
     * @return
     */
    public static MessageMarshallingDecode buildMarshallingDecoder(){
        final MarshallerFactory marshallerFactory = Marshalling.
                getProvidedMarshallerFactory("serial");// java序列化对象
        final MarshallingConfiguration configuration = new MarshallingConfiguration();
        configuration.setVersion(5);
        UnmarshallerProvider provider = new DefaultUnmarshallerProvider(marshallerFactory, configuration);
        MessageMarshallingDecode decoder = new MessageMarshallingDecode(provider, 1024);// 单个消息长度1024kb
        return decoder;
    }
}
