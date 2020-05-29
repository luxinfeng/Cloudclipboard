package com.luxinfeng.cloudclipboard.WebSocket.model;


import lombok.Data;
import lombok.Setter;

@Data
public class NormalUserInfo {
    private final String clientIp;
    private final String loginTime;
    private final String logoutTime;
    private final int clientMaxSum;
    private final int clientAvgSum;
    private final long MessageSum;
}
