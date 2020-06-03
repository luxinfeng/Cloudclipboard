package com.luxinfeng.cloudclipboard.WebSocket.Common;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;

import java.util.LinkedList;
import java.util.List;


@Slf4j
public class SaveHistoryInfo {
    private static Jedis jedis = new Jedis("127.0.0.1", 6379);

    public void setHistory(String code,String value){
        String historyCode = "history" + code;

        if(jedis.llen(historyCode)>4){
            jedis.lpop(historyCode);
        }
        jedis.lpush(historyCode,value);
        log.info(code +"该记录已暂时性保存");
    }

    public List<String> getHistory(String code){
        List<String> res = new LinkedList<>();
        String historyCode = "history" + code;
        long len = jedis.llen(historyCode);
        if(len>0){
            for(long i=0;i<len;i++){
                res.add(jedis.lindex(historyCode,i));
            }
        }
        return res;
    }
}
