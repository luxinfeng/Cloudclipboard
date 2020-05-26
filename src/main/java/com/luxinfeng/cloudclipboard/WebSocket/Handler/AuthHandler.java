package com.luxinfeng.cloudclipboard.WebSocket.Handler;

import com.alibaba.fastjson.JSONObject;
import com.luxinfeng.cloudclipboard.WebSocket.LoginConfig.CodeContainer;
import com.luxinfeng.cloudclipboard.WebSocket.util.LoginCode;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

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
                System.out.println(code);
                codeContainer.addCode(code);
                channelHandlerContext.writeAndFlush(new TextWebSocketFrame("code"+code));
                System.out.println("成功获取登录码");
            }else if(type.equals("login")){
                String code = jsonObject.get("token").toString();
                if(codeContainer.containsCode(code)==null){
                    System.out.println("当前登录码无效，请重新获取登录码");
                    channelHandlerContext.close();
                }else{
                    codeContainer.addUser(code,channelHandlerContext);
                    System.out.println("登录成功");
                    channelHandlerContext.pipeline().remove(this);
                }
            }
        } catch (Exception e) {
            System.out.println("exception happen for:" + e.getMessage());
            channelHandlerContext.writeAndFlush("clip请稍后再试");
            channelHandlerContext.close();
        }
    }
}
