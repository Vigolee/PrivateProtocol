package main;

import coder.MessageDecoder;
import coder.MessageEncoder;
import config.Configuration;
import handler.auth.LoginAuthReqHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * 客户端
 * Created by Vigo on 16/4/28.
 */
public class Client {
    private ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

    EventLoopGroup group = new NioEventLoopGroup();

    public void connect(String host, int port) {
        try {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group).channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel channel) throws Exception {
                        channel.pipeline().addLast(new MessageDecoder(1024 * 1024, 4, 4, -8, 0))
                                .addLast("MessageEncoder", new MessageEncoder())
                                .addLast("LoginAuthReqHandler", new LoginAuthReqHandler());
                             //   .addLast("HeartBeatHandler", new HeartBeatReqHandler());
                    }
                });
        // 发起异步连接
            ChannelFuture future = bootstrap.connect(new InetSocketAddress(host, port)).sync();
            future.channel().closeFuture().sync();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        finally {

            // 释放资源，重连
//            executor.execute(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                        TimeUnit.SECONDS.sleep(5);
//                        connect(Configuration.IP, Configuration.PORT);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//
//                }
//            });
        }
    }

    public static void main(String[] args) {
        new Client().connect(Configuration.IP, Configuration.PORT);
    }
}
