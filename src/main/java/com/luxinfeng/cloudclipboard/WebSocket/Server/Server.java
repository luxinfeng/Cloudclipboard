package com.luxinfeng.cloudclipboard.WebSocket.Server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.util.concurrent.ImmediateEventExecutor;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;


/**
 * @author 新峰
 */

@Slf4j
public class Server {
    private final ChannelGroup channelGroup =
            new DefaultChannelGroup(ImmediateEventExecutor.INSTANCE);
    private final EventLoopGroup group = new NioEventLoopGroup();
    private Channel channel;

    public ChannelFuture start(InetSocketAddress address) throws InterruptedException {
        ServerBootstrap bootstrap = new ServerBootstrap();
        NioEventLoopGroup bossGroup = new NioEventLoopGroup(0, new DefaultThreadFactory("boss"));
        NioEventLoopGroup workGroup = new NioEventLoopGroup(0, new DefaultThreadFactory("worker"));
        bootstrap.group(bossGroup,workGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(createInitializer(channelGroup));
        ChannelFuture future = bootstrap.bind(address).sync();
        future.syncUninterruptibly();
        channel = future.channel();
        return future;
    }

    protected ChannelInitializer<Channel> createInitializer(
            ChannelGroup group) {
        return new ServerInitializer(group);
    }

    public void destroy() {
        if (channel != null) {
            channel.close();
        }
        channelGroup.close();
        group.shutdownGracefully();
    }

    public static void main(String[] args) throws Exception {
//        int port = 443;
        int port = 8090;
        final Server endpoint = new Server();
        ChannelFuture future = endpoint.start(
                new InetSocketAddress(port));
        log.info("启动成功！");
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                endpoint.destroy();
            }
        });
        future.channel().closeFuture().sync();
    }
}

