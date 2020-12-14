package com.luxinfeng.cloudclipboard.WebSocket.Handler;

import com.alibaba.fastjson.JSONObject;
import com.luxinfeng.cloudclipboard.WebSocket.LoginConfig.CodeContainer;
import com.luxinfeng.cloudclipboard.WebSocket.Util.LoginCode;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@ChannelHandler.Sharable
public class AuthHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, TextWebSocketFrame textWebSocketFrame) throws Exception {
        try {
            JSONObject jsonObject;
            jsonObject = JSONObject.parseObject(textWebSocketFrame.text());
            String type=jsonObject.get("type").toString();

            CodeContainer codeContainer = new CodeContainer();
            if(type.equals("code")){
                LoginCode logincode = new LoginCode();
                String code = logincode.getCode();
                codeContainer.addCode(code);
                channelHandlerContext.writeAndFlush(new TextWebSocketFrame("登录码为："+code));
                log.info("成功获取登录码:"+code);
            }else if(type.equals("login")){
                String code = jsonObject.get("token").toString();
                if(codeContainer.containsCode(code)==null){
                    channelHandlerContext.writeAndFlush(new TextWebSocketFrame("当前登录码无效，请重新获取登录码"));
                    log.error("当前登录码无效，请重新获取登录码");
                    channelHandlerContext.close();
                }else{
                    codeContainer.addUser(code,channelHandlerContext);
                    channelHandlerContext.writeAndFlush(new TextWebSocketFrame("登录成功"));
                    log.info("登录成功");
                    channelHandlerContext.pipeline().remove(this);
                }
            }
        } catch (Exception e) {
            System.out.println("exception happen for:" + e.getMessage());
            channelHandlerContext.writeAndFlush("请稍后再试");
            log.error("请稍后再试");
            channelHandlerContext.close();
        }
    }
}
