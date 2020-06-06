package com.luxinfeng.cloudclipboard.WebSocket.Server;

import com.luxinfeng.cloudclipboard.WebSocket.Handler.*;
import com.luxinfeng.cloudclipboard.WebSocket.Util.SslContextFactory;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.util.concurrent.UnorderedThreadPoolEventExecutor;
import java.io.InputStream;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.util.Enumeration;


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


        //SSL加密
//        String jksPath = "D:\\Java\\serverStore.jks";
//        String jksPath = "/home/xinfeng/cloudclipboard/serverStore.jks";
//        SSLContext sslContext = SslContextFactory.getServerContext(jksPath,"nettyDemo","123456");

//        SSLEngine sslEngine = sslContext.createSSLEngine();
//        sslEngine.setUseClientMode(false);
//        sslEngine.setNeedClientAuth(false);
//        SslHandler sslHandler = new SslHandler(sslEngine);
//
//        pipeline.addLast(sslHandler);
        //连接数统计
        pipeline.addLast(metricsHandler);
        //定时清除空闲连接
        pipeline.addLast(new IdleCheckHandler());
        pipeline.addLast(new HttpServerCodec());
        pipeline.addLast(new ChunkedWriteHandler());
        pipeline.addLast(new HttpObjectAggregator(64 * 1024));
        pipeline.addLast(new HttpRequestHandler("/ws"));
        pipeline.addLast(new WebSocketServerProtocolHandler("/ws"));
        AuthHandler authHandler = new AuthHandler();
        pipeline.addLast(authHandler);
        pipeline.addLast(businessGroup,new TextWebSocketFrameHandler(group));
    }
}

