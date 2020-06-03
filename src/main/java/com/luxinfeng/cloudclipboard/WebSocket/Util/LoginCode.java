package com.luxinfeng.cloudclipboard.WebSocket.Util;


public class LoginCode {
    public String getCode(){
        String timeCode = String.valueOf(System.currentTimeMillis());
        return timeCode.substring(5);
    }
}
