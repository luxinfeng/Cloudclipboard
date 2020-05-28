package com.luxinfeng.cloudclipboard.WebSocket.Server;

import com.luxinfeng.cloudclipboard.WebSocket.Handler.*;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.util.concurrent.UnorderedThreadPoolEventExecutor;


/**
 * @author 新峰
 */
public class ServerInitializer extends ChannelInitializer<Channel> {
    private final ChannelGroup group;

    public ServerInitializer(ChannelGroup group) {
        this.group = group;
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {

        UnorderedThreadPoolEventExecutor businessGroup = new UnorderedThreadPoolEventExecutor(2, new DefaultThreadFactory("business"));
        MetricsHandler metricsHandler = new MetricsHandler();
//        BlackListHandler blackListHandler = new BlackListHandler();
        ChannelPipeline pipeline = ch.pipeline();
        //连接数统计
        pipeline.addLast(metricsHandler);
        pipeline.addLast(new HttpServerCodec());
        pipeline.addLast(new ChunkedWriteHandler());
//        pipeline.addLast(blackListHandler);
        pipeline.addLast(new HttpObjectAggregator(64 * 1024));
//        pipeline.addLast(blackListHandler);
        pipeline.addLast(new HttpRequestHandler("/ws"));
        //定时清除空闲连接
        pipeline.addLast(new IdleCheckHandler());
        pipeline.addLast(new WebSocketServerProtocolHandler("/ws"));
        AuthHandler authHandler = new AuthHandler();
        pipeline.addLast(authHandler);
//        pipeline.addLast(new ConnectHandler());
        pipeline.addLast(businessGroup,new TextWebSocketFrameHandler(group));
    }
}

