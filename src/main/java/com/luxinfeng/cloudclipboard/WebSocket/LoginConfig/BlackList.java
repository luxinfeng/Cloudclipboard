package com.luxinfeng.cloudclipboard.WebSocket.LoginConfig;

import com.luxinfeng.cloudclipboard.WebSocket.Util.RedisClient;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;

import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class BlackList {
    private static Jedis jedis = RedisClient.getJedis();
    private static ConcurrentHashMap<String,Integer> blackList = new ConcurrentHashMap<>();


    public void put(String clientIp){
        String mayBlackListIp = "mayBlackList" + clientIp;
        log.info(mayBlackListIp);
        if(jedis.get(mayBlackListIp)==null){
            jedis.setex(mayBlackListIp,60,clientIp);
        }
        int count = blackList.getOrDefault(mayBlackListIp,0);
        blackList.put(mayBlackListIp,count+1);
        if(jedis.get(mayBlackListIp)!=null&&count>=19){
            jedis.setex(mayBlackListIp,86400,clientIp);
            blackList.remove(mayBlackListIp);
            jedis.del(mayBlackListIp);
        }
    }

    public boolean inBlackList(String clientIp){
//        clientIp = "mayBlackList" + clientIp;
//        String ans = jedis.get(clientIp);
//        if(ans!=null){
//            return true;
//        }else{
//            return false;
//        }
        return false;
    }



}
