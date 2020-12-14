package com.luxinfeng.cloudclipboard.WebSocket.Common;

import com.luxinfeng.cloudclipboard.WebSocket.Model.AbnormalUserInfo;
import com.luxinfeng.cloudclipboard.WebSocket.Model.NormalUserInfo;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

@Slf4j
public class SaveInfo {

    private final static String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    private final static String DB_URL = "jdbc:mysql://localhost:3306/RUNOOB?useSSL=false&serverTimezone=UTC";
    private static Connection connection;
    private static Statement statement;

    private final static String USER = "xinfeng";
    private final static String PASS = "xinfeng666";


    static {
        try{
            Class.forName(JDBC_DRIVER);
            connection = DriverManager.getConnection(DB_URL, USER, PASS);
            statement = connection.createStatement();
            if (!connection.isClosed()) {
                log.info("数据库连接成功");
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    public void saveInfo(AbnormalUserInfo abnormalUserInfo) throws SQLException {
        String clientIp = abnormalUserInfo.getClientIp();
        String loginTime = abnormalUserInfo.getLoginTime();
        String sql ="INSERT INTO abnormalUserInfo VALUES " + "("+clientIp + loginTime +")";
//        statement.execute(sql);
        log.info("异常登录信息已保存到数据库");
    }
    public void saveInfo(NormalUserInfo normalUserInfo) throws SQLException {
        String clientIp = normalUserInfo.getClientIp();
        String loginTime = normalUserInfo.getLoginTime();
        String logoutTime = normalUserInfo.getLogoutTime();
        int clientMaxSum = normalUserInfo.getClientMaxSum();
        int clientAvgSum = normalUserInfo.getClientAvgSum();
        long MessageSum = normalUserInfo.getMessageSum();
        String sql ="INSERT INTO abnormalUserInfo VALUES " + "("+clientIp + loginTime + logoutTime + clientMaxSum + clientAvgSum + MessageSum +")";
        statement.execute(sql);
        log.info("正常登录信息已保存到数据库");
    }

}
