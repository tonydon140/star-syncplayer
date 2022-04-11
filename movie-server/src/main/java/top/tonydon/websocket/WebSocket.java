package top.tonydon.websocket;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import top.tonydon.entity.Message;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

// 接受 websocket 请求路径
@ServerEndpoint(value = "/websocket")
// 注册到 spring 容器中
@Component
@Slf4j
public class WebSocket {


    //保存所有在线socket连接
    private static Map<String, WebSocket> webSocketMap = new LinkedHashMap<>();

    //记录当前在线数目
    private static int count = 0;

    //当前连接（每个websocket连入都会创建一个MyWebSocket实例
    private Session session;

    // 处理连接建立
    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        webSocketMap.put(session.getId(), this);

        try {
            String json = JSON.toJSONString(new Message(1, "Hello"));
            session.getBasicRemote().sendText(json);
        } catch (IOException e) {
            e.printStackTrace();
        }

        addCount();
        log.info("新的连接加入：{}", session.getId());
    }

    //接受消息
    @OnMessage
    public void onMessage(String message, Session session) {
        log.info("收到客户端{}消息：{}", session.getId(), message);
        broadcast(session.getId(), message);
    }

    //处理错误
    @OnError
    public void onError(Throwable error, Session session) {
        log.info("发生错误{}, {}", session.getId(), error.getMessage());
    }

    //处理连接关闭
    @OnClose
    public void onClose() {
        webSocketMap.remove(this.session.getId());
        reduceCount();
        log.info("连接关闭:{}", this.session.getId());
    }

    //群发消息

    //发送消息
    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }

    // 广播消息
    public static void broadcast() {
        WebSocket.webSocketMap.forEach((k, v) -> {
            try {
                v.sendMessage("这是一条测试广播");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * 群发消息
     * @param message 消息内容
     */
    public void broadcast(String message){
        webSocketMap.forEach((k, v) -> {
            try {
                v.sendMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }


    public void broadcast(String id, String message){
        webSocketMap.forEach((k, v) -> {
            try {
                if(!k.equals(id))
                    v.sendMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
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
