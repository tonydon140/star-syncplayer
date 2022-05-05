星星电影院，异地恋情侣双方可以同时控制播放电影的桌面/服务程序。



#### 如何使用

解压 XingXingMovie.zip 压缩包，在 bin 目录中运行【星星电影院.exe】即可。

客户端的功能：

- 视频播放器。选择本地视频文件进行播放，可控制声音、倍速、进度和全屏；
- 网络双方同时播放视频。连接服务器之后，双方选择好相同的本地视频，即可进行同步播放功能；
- 弹幕互动功能，双方进行绑定之后可以进行弹幕互动；

你可以搭建自己的服务器，只需要将 movie-server 的打包结果运行在自己服务器中即可。软件中提供了一个默认的服务器地址，该服务器在未来可能会有变动。

![image-20220505165214873](readme.assets/image-20220505165214873.png)

支持的视频格式：由于 JavaFX 的限制，目前只支持 MP4 和 FLV 格式。



#### 项目模块介绍

##### movie-client

使用 JavaFX 和 Java-WebSocket 实现的客户端播放器



##### movie-server

基于 SpringBoot 的 WebSocket 服务端



##### websocket-client

WebSocket 的客户端实现。movie-client 模块中的 WebSocket 客户端使用 Java-Websocket 实现。

Java-Websocket 的项目地址：https://github.com/TooTallNate/Java-WebSocket

但是由于 Java-Websocket 没有引入模块化，而 JavaFX 是模块化应用，所以我下载源码将其打包为模块化，并使用了模块化的 Slf4j。



