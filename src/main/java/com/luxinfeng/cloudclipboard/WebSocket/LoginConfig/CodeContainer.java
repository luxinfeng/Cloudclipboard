package com.luxinfeng.cloudclipboard.WebSocket.LoginConfig;

import io.netty.channel.ChannelHandlerContext;
import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

public class CodeContainer {
    private static HashMap<String, List<ChannelHandlerContext>> map = new HashMap<>();
    private static HashSet<String> set = new HashSet<>();
    private static Jedis jedis = new Jedis("127.0.0.1", 6379);

    public void addCode(String str){
        jedis.setex(str,1800,str);
    }
    public String containsCode(String code){
        return jedis.get(code);
    }

    public void addUser(String code,ChannelHandlerContext ctx){
        List list = map.getOrDefault(code,new LinkedList<ChannelHandlerContext>());
        list.add(ctx);
        map.put(code,list);
    }

    public List<ChannelHandlerContext> getUser(String code){
        return map.get(code);
    }
}
