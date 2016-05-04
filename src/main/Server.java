package main;

import coder.MessageDecoder;
import coder.MessageEncoder;
import config.Configuration;
import handler.auth.LoginAuthRespHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * 服务器
 * Created by Vigo on 16/4/28.
 */
public class Server {

    public void bind() {
        EventLoopGroup mainGroup = new NioEventLoopGroup();
        EventLoopGroup workGroup = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(mainGroup, workGroup).channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 100)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        // -8表示lengthAdjustment，让解码器从0开始截取字节，并且包含消息头
                        // 消息长度在第4字节，长度占位4字节，消息长度＝消息头 + 消息体，修正（4 + 4 = 8）
                        socketChannel.pipeline().addLast(new MessageDecoder(1024 * 1024, 4, 4, -8, 0))
                                .addLast(new MessageEncoder())
                                .addLast(new LoginAuthRespHandler());
                      //          .addLast(new HeartBeatRespHandler());
                    }
                });
        try {
            ChannelFuture future = bootstrap.bind(Configuration.IP, Configuration.PORT).sync();
            System.out.println("server is start");
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        new Server().bind();
    }
}
