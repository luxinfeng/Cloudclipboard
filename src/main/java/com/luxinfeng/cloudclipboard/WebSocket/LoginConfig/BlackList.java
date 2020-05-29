package com.luxinfeng.cloudclipboard.WebSocket.LoginConfig;

import redis.clients.jedis.Jedis;

import java.util.concurrent.ConcurrentHashMap;

public class BlackList {
    private static Jedis jedis = new Jedis("127.0.0.1", 6379);
    private static ConcurrentHashMap<String,Integer> blackList = new ConcurrentHashMap<>();


    public void put(String clientIp){
        String mayBlackListIp = "mayBlackList" + clientIp;
        if(jedis.get(mayBlackListIp)==null){
            jedis.setex(mayBlackListIp,60,clientIp);
        }
        int count = blackList.getOrDefault(clientIp,0);
        blackList.put(clientIp,count+1);
        if(jedis.get(mayBlackListIp)!=null&&count>=9){
            jedis.setex(clientIp,86400,clientIp);
            blackList.remove(clientIp);
            jedis.del(mayBlackListIp);
        }
    }

    public boolean inBlackList(String clientIp){
        String ans = jedis.get(clientIp);
        if(ans!=null){
            return true;
        }else{
            return false;
        }
//        return false;
    }



}
