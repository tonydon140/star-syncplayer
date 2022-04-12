package top.tonydon.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import top.tonydon.message.server.ConnectMessage;
import top.tonydon.message.JsonMessage;
import top.tonydon.message.Message;
import top.tonydon.util.WebSocketGroup;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

// 接受 websocket 请求路径
@ServerEndpoint(value = "/websocket")
// 注册到 spring 容器中
@Component
@Slf4j
public class WebSocket {


    //保存所有在线socket连接
//    private static Map<String, WebSocket> webSocketMap = new LinkedHashMap<>();

    /**
     * key：星星号
     * value：WebSocketGroup
     */
    private static final Map<String, WebSocketGroup> map = new HashMap<>();

    //记录当前在线数目
    private static int count = 0;

    //当前连接（每个websocket连入都会创建一个MyWebSocket实例
    private Session session;

    // 处理连接建立
    @OnOpen
    public void onOpen(Session session) {
        // 1. 保存 session，生成星星号
        this.session = session;
        ConnectMessage message = ConnectMessage.success();

        // 2. 存储到 map 中
        map.put(message.getNumber(), new WebSocketGroup(this, null));

        // 3. 返回连接消息
        try {
            session.getBasicRemote().sendText(message.toJson());
        } catch (IOException e) {
            e.printStackTrace();
        }

        addCount();
        log.info("新的连接加入：{}", session.getId());
    }

    //接受消息
    @OnMessage
    public void onMessage(String json, Session session) {
//        log.info("收到客户端{}消息：{}", session.getId(), message);
        Message message = JsonMessage.parse(json);

        if(message.getType() == )
        log.info("收到客户端{}消息：{}", session.getId(), message);
    }

    //处理错误
    @OnError
    public void onError(Throwable error, Session session) {
        log.info("发生错误{}, {}", session.getId(), error.getMessage());
    }

    //处理连接关闭
    @OnClose
    public void onClose() {
        map.remove(this.session.getId());
        reduceCount();
        log.info("连接关闭:{}", this.session.getId());
    }

    //群发消息

    //发送消息
    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }

    //获取在线连接数目
    public static int getCount() {
        return count;
    }

    //操作count，使用synchronized确保线程安全
    public static synchronized void addCount() {
        WebSocket.count++;
    }

    public static synchronized void reduceCount() {
        WebSocket.count--;
    }
}
