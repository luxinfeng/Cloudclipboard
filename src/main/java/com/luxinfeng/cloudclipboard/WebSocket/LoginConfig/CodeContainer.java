package com.luxinfeng.cloudclipboard.WebSocket.LoginConfig;

import io.netty.channel.ChannelHandlerContext;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

public class CodeContainer {
    private static HashMap<String, List<ChannelHandlerContext>> map = new HashMap<>();
    private static HashSet<String> set = new HashSet<>();

    public void addCode(String str){
        set.add(str);
    }
    public boolean containsCode(String code){
        return set.contains(code);
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
