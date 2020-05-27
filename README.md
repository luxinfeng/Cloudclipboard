## 简介

**Cloudclipboard**

这是一个基于Netty和WebSocket的云剪贴板。该剪贴板使用网页登录，借助该剪贴板，我们可以实现在多个平台之间传递剪贴板的内容。
目前只实现了核心功能，只能算是一个比较简陋的demo。接下来会不断更新的。

## 项目相关
- [项目研发日志更新](https://github.com/luxinfeng/Cloudclipboard/blob/dev/doc/Project-Log-cn.md)
- [项目设计思路](https://github.com/luxinfeng/cloudclipboard/blob/dev/doc/design_cn.md)
- [项目文档](https://github.com/luxinfeng/Cloudclipboard/wiki/_new)

## Done List

- 设置用户登录时长限制
- 登录存储，退出删除（一定时间后自动清除）


## To Do List
- ~~登录存储，退出删除~~
- 用户认证机制（限制用户登录数，考虑加入黑名单功能）
- ~~设置用户登录时长限制~~
- ~~心跳机制和idle检测~~
- ~~主从工作模式~~
- ~~加入日志系统~~
- 加入系统诊断功能
- 解决消息的可靠投递问题 
- 参数调优
- 增加使用账户密码登录方式
- 加密通信
- 保存用户的一定数量的历史记录
- 后期会考虑搭建Netty集群



