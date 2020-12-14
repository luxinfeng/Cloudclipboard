package com.example.netty;

import redis.clients.jedis.Jedis;

public class JedisTest {
    private Jedis jedis;

    public static void main(String[] args) {
        JedisTest jedisTest = new JedisTest();
        jedisTest.jedis = new Jedis("127.0.0.1",6379);
        jedisTest.jedis.setex("apple",3,"apple");
        while(true){
            System.out.println(jedisTest.jedis.get("apple"));
        }

    }

}
