星星电影院，异地恋情侣双方可以同时控制播放电影的桌面/服务程序。



### 如何使用

解压 StarMovie.zip 压缩包，在 bin 目录中运行【星星电影院.exe】即可。

客户端的功能：

- 视频播放器。选择本地视频文件进行播放，可控制声音、倍速、进度和全屏；
- 网络双方同时播放视频。连接服务器之后，双方选择好相同的本地视频，即可进行同步播放功能；
- 弹幕互动功能，双方进行绑定之后可以进行弹幕互动；

![image-20220505165214873](assets/image-20220505165214873.png)

#### 服务器

软件中提供了一个长期的服务器。你也可以参考 movie-server 模块搭建自己的服务器。



#### 视频格式

支持的视频格式：由于 JavaFX 的限制，目前只支持 MP4 和 FLV 格式。



### 项目模块介绍

| 模块             | 描述                                                         |
| ---------------- | ------------------------------------------------------------ |
| movie-client     | 使用 JavaFX 和 Java-WebSocket 实现的客户端播放器             |
| movie-common     | client 和 server 之间使用的消息实体类                        |
| movie-server     | 基于 SpringBoot 的 WebSocket 服务端                          |
| websocket-client | 模块化的 WebSocket 的客户端实现。基于 [Java-Websocket](https://github.com/TooTallNate/Java-WebSocket) |





