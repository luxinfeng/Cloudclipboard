package com.luxinfeng.cloudclipboard.WebSocket.Handler;

import com.alibaba.fastjson.JSONObject;
import com.luxinfeng.cloudclipboard.WebSocket.Common.SaveHistoryInfo;
import com.luxinfeng.cloudclipboard.WebSocket.LoginConfig.CodeContainer;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author 新峰
 */
@Slf4j
@ChannelHandler.Sharable
public class TextWebSocketFrameHandler
        extends SimpleChannelInboundHandler<TextWebSocketFrame> {
    private final ChannelGroup group;
    private static SaveHistoryInfo saveHistoryInfo;

    public TextWebSocketFrameHandler(ChannelGroup group) {
        this.group = group;
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx,
                                   Object evt) throws Exception {
        if (evt == WebSocketServerProtocolHandler
                .ServerHandshakeStateEvent.HANDSHAKE_COMPLETE) {
            ctx.pipeline().remove(HttpRequestHandler.class);

            group.add(ctx.channel());
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx,
                             TextWebSocketFrame msg) throws Exception {


        saveHistoryInfo = new SaveHistoryInfo();
        JSONObject jsonObject;
        jsonObject = JSONObject.parseObject(msg.text());
        String type=jsonObject.get("type").toString();
        CodeContainer codeContainer = new CodeContainer();

        if(type.equals("sendGroup")){
            String code = jsonObject.get("token").toString();
            saveHistoryInfo.setHistory(code,jsonObject.get("value").toString());
            log.info("写入Redis成功");
            if(codeContainer.containsCode(code)!=null){
                List<ChannelHandlerContext> list = codeContainer.getUser(code);
                log.info("当前该用户的设备数："+String.valueOf(list.size()));
                for(ChannelHandlerContext user:list){
                    log.info(jsonObject.get("value").toString());
                    user.writeAndFlush(new TextWebSocketFrame(jsonObject.get("value").toString()));

                }
                log.info("成功发送到多设备");
            }
        }else if(type.equals("history")){
            log.info("获取历史记录");
            String code = jsonObject.get("token").toString();
            List<String> ans = saveHistoryInfo.getHistory(code);
            String historyString="";
            for(String str:ans){
                historyString = historyString + str + "\n";
            }
            ctx.writeAndFlush(new TextWebSocketFrame(historyString));

        }
        else if(type.equals("heartCheck")){
//            ctx.writeAndFlush(new TextWebSocketFrame("heartCheck reponse"));
            log.info("This is a heartCheck");
        }
    }


}
