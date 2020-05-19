package com.example.netty.WebSocket.util;


public class LoginCode {
    public String getCode(){
        String timeCode = String.valueOf(System.currentTimeMillis());
        return timeCode.substring(5);
    }

    public static void main(String[] args) {
        LoginCode loginCode = new LoginCode();
        System.out.println(loginCode.getCode());
    }
}
