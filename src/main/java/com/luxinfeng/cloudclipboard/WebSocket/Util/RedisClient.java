package com.luxinfeng.cloudclipboard.WebSocket.Util;



import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;



public class RedisClient {

    public static Jedis getJedis(){
        return inner.instance;
    }
    private static class inner{
        private static final Jedis instance = new Jedis("127.0.0.1", 6379,0,0);

    }
}
