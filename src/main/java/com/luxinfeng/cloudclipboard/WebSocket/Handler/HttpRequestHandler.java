package com.luxinfeng.cloudclipboard.WebSocket.Handler;

import com.luxinfeng.cloudclipboard.WebSocket.Common.SaveInfo;
import com.luxinfeng.cloudclipboard.WebSocket.LoginConfig.BlackList;
import com.luxinfeng.cloudclipboard.WebSocket.model.AbnormalUserInfo;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.stream.ChunkedNioFile;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.net.URISyntaxException;
import java.net.URL;


/**
 * @author 新峰
 */
@Slf4j
@ChannelHandler.Sharable
public class HttpRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    private final String wsUri;
    private static final File INDEX;
    private static SaveInfo saveInfo;
    private BlackList blackList = new BlackList();

    static {
        URL location = HttpRequestHandler.class
                .getProtectionDomain()
                .getCodeSource().getLocation();
        saveInfo = new SaveInfo();
        try {
            String path = location.toURI() + "index.html";
            path = !path.contains("file:") ? path : path.substring(5);
            INDEX = new File(path);
        } catch (URISyntaxException e) {
            throw new IllegalStateException(
                    "Unable to locate index.html", e);
        }
    }

    public HttpRequestHandler(String wsUri) {
        this.wsUri = wsUri;
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx,
                             FullHttpRequest request) throws Exception {

        log.info("into HttpRequestHandler");
        if(request instanceof FullHttpRequest){
            HttpRequest mReq = request;
            String clientIp = mReq.headers().get("X-Forwarded-For");
            if(clientIp==null){
                InetSocketAddress insocket = (InetSocketAddress) ctx.channel().remoteAddress();
                clientIp = insocket.getAddress().getHostAddress();
                log.info(clientIp);
            }
            if(blackList.inBlackList(clientIp)){
                log.error("短时间内多次登录，登录失效");
                AbnormalUserInfo abnormalUserInfo = new AbnormalUserInfo(clientIp,String.valueOf(System.currentTimeMillis()));
                saveInfo.saveInfo(abnormalUserInfo);
                ctx.writeAndFlush("登录失效，请24小时后登录");
                ctx.close();
            }else{
                blackList.put(clientIp);
            }
        }


        if (wsUri.equalsIgnoreCase(request.getUri())) {
            ctx.fireChannelRead(request.retain());
        } else {
            if (HttpHeaders.is100ContinueExpected(request)) {
                send100Continue(ctx);
            }
            RandomAccessFile file = new RandomAccessFile(INDEX, "r");
            HttpResponse response = new DefaultHttpResponse(
                    request.getProtocolVersion(), HttpResponseStatus.OK);
            response.headers().set(
                    HttpHeaders.Names.CONTENT_TYPE,
                    "text/html; charset=UTF-8");
            boolean keepAlive = HttpHeaders.isKeepAlive(request);
            if (keepAlive) {
                response.headers().set(
                        HttpHeaders.Names.CONTENT_LENGTH, file.length());
                response.headers().set( HttpHeaders.Names.CONNECTION,
                        HttpHeaders.Values.KEEP_ALIVE);
            }
            ctx.write(response);
            if (ctx.pipeline().get(SslHandler.class) == null) {
                ctx.write(new DefaultFileRegion(
                        file.getChannel(), 0, file.length()));
            } else {
                ctx.write(new ChunkedNioFile(file.getChannel()));
            }
            ChannelFuture future = ctx.writeAndFlush(
                    LastHttpContent.EMPTY_LAST_CONTENT);
            if (!keepAlive) {
                future.addListener(ChannelFutureListener.CLOSE);
            }
        }
    }

    private static void send100Continue(ChannelHandlerContext ctx) {
        FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1, HttpResponseStatus.CONTINUE);
        ctx.writeAndFlush(response);
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        cause.printStackTrace();
        log.error("Http request error");
        ctx.close();
    }

}

