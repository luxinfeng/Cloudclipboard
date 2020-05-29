package com.luxinfeng.cloudclipboard.WebSocket.Common;

import com.luxinfeng.cloudclipboard.WebSocket.model.AbnormalUserInfo;
import com.luxinfeng.cloudclipboard.WebSocket.model.NormalUserInfo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class SaveInfo {

    private final static String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    private final static String DB_URL = "jdbc:mysql://localhost:3306/RUNOOB?useSSL=false&serverTimezone=UTC";
    private static Connection connection;
    private static Statement statement;

    private final static String USER = "root";
    private final static String PASS = "123456";


    static {
        try{
            Class.forName(JDBC_DRIVER);
            connection = DriverManager.getConnection(DB_URL, USER, PASS);
            statement = connection.createStatement();
            if (!connection.isClosed()) {
                System.out.println("数据库连接成功");
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
        statement.execute(sql);
    }
    public void saveinfo(NormalUserInfo normalUserInfo) throws SQLException {
        String clientIp = normalUserInfo.getClientIp();
        String loginTime = normalUserInfo.getLoginTime();
        String logoutTime = normalUserInfo.getLogoutTime();
        int clientMaxSum = normalUserInfo.getClientMaxSum();
        int clientAvgSum = normalUserInfo.getClientAvgSum();
        long MessageSum = normalUserInfo.getMessageSum();
        String sql ="INSERT INTO abnormalUserInfo VALUES " + "("+clientIp + loginTime + logoutTime + clientMaxSum + clientAvgSum + MessageSum +")";
        statement.execute(sql);
    }

}
