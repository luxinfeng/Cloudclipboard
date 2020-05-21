package com.luxinfeng.cloudclipboard.WebSocket.Handler;

import com.alibaba.fastjson.JSONObject;
import com.luxinfeng.cloudclipboard.WebSocket.LoginConfig.CodeContainer;
import com.luxinfeng.cloudclipboard.WebSocket.util.LoginCode;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;

import java.util.List;

/**
 * @author 新峰
 */

@ChannelHandler.Sharable
public class TextWebSocketFrameHandler
        extends SimpleChannelInboundHandler<TextWebSocketFrame> {
    private final ChannelGroup group;

    public TextWebSocketFrameHandler(ChannelGroup group) {
        this.group = group;
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx,
                                   Object evt) throws Exception {
        if (evt == WebSocketServerProtocolHandler
                .ServerHandshakeStateEvent.HANDSHAKE_COMPLETE) {
            ctx.pipeline().remove(HttpRequestHandler.class);

//            group.writeAndFlush(new TextWebSocketFrame(
//                    "Client " + ctx.channel()+ " joined"));
            group.add(ctx.channel());
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx,
                             TextWebSocketFrame msg) throws Exception {
        JSONObject jsonObject;
        jsonObject = JSONObject.parseObject(msg.text());
        String type=jsonObject.get("type").toString();

        CodeContainer codeContainer = new CodeContainer();
        if(type.equals("code")){
            LoginCode logincode = new LoginCode();
            String code = logincode.getCode();
            System.out.println(code);
            codeContainer.addCode(code);
//            ctx.channel().writeAndFlush(Unpooled.copiedBuffer(code, CharsetUtil.UTF_8));
            ctx.channel().writeAndFlush(new TextWebSocketFrame("code"+code));
            System.out.println("成功获取登录码");
        }else if(type.equals("login")){
            String code = jsonObject.get("token").toString();
            if(!codeContainer.containsCode(code)){
                throw new IllegalStateException("当前登录码无效，请重新申请");
            }else{
                codeContainer.addUser(code,ctx);
                System.out.println("登录成功");
            }
        }else if(type.equals("sendGroup")){
            System.out.println("成功发送到多设备");
            String code = jsonObject.get("token").toString();
            if(codeContainer.containsCode(code)){
                List<ChannelHandlerContext> list = codeContainer.getUser(code);
                System.out.println(list.size());
                for(ChannelHandlerContext user:list){
//                    ByteBuf buf = Unpooled.copiedBuffer(jsonObject.get("value").toString(), CharsetUtil.UTF_8);
                    System.out.println(jsonObject.get("value"));
                    user.channel().writeAndFlush(new TextWebSocketFrame("clip"+jsonObject.get("value").toString()));

                }
            }
        }
//        group.writeAndFlush(msg.retain());
    }


}
