## 项目设计思路
### 设计原则
在Netty的基础上开发了HTTP服务器与WebSocket服务器，借助Netty的NIO特性实现高新能的服务器。

### 登录设计
目前是先用HTTP建立连接，然后改用WebSocket连接。
