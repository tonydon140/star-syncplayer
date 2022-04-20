星星电影院，异地恋情侣双方可以同时控制播放电影的桌面/服务程序

###### movie-client

使用 JavaFX 和 Java-WebSocket 实现的客户端播放器



###### movie-server

基于 SpringBoot 的 WebSocket 服务端



###### websocket-client

WebSocket 的客户端实现。movie-client 模块中的 WebSocket 客户端使用 Java-Websocket 实现。

Java-Websocket 的项目地址：https://github.com/TooTallNate/Java-WebSocket

但是由于 Java-Websocket 没有引入模块化，而 JavaFX 是模块化应用，所以我下载源码将其打包为模块化，并删除了源码中的 Slf4j 依赖，作为 WebSocket 的客户端来说，并不影响使用。