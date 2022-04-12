package top.tonydon.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import top.tonydon.message.client.BindMessage;
import top.tonydon.message.client.MovieMessage;
import top.tonydon.message.server.ConnectMessage;
import top.tonydon.message.JsonMessage;
import top.tonydon.message.Message;
import top.tonydon.message.server.ResponseMessage;
import top.tonydon.message.server.ServerBindMessage;
import top.tonydon.util.MessageType;
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
    public void onMessage(String json, Session session) throws IOException {
        Message message = JsonMessage.parse(json);

        // 1. 绑定消息
        if (message.getType() == MessageType.BIND_TYPE) doBind(message, session);
            // 2. 电影消息
        else if (message.getType() == MessageType.MOVIE_TYPE) doMovie(message);

        log.info("{} --- {}", session.getId(), message);
    }

    /**
     * 处理绑定消息
     *
     * @param message 消息
     */
    private void doBind(Message message, Session session) throws IOException {
        BindMessage bindMessage = (BindMessage) message;

        // 1. 根据星星号获取组
        WebSocketGroup self = map.get(bindMessage.getSelfNumber());
        if (self == null) {
            session.getBasicRemote().sendText(ResponseMessage.error("本机星星号不存在").toJson());
            return;
        }

        // 3. 获取她/他的星星号
        WebSocketGroup target = map.get(bindMessage.getTargetNumber());
        if (target == null) {
            session.getBasicRemote().sendText(ResponseMessage.error("远程端星星号不存在").toJson());
            return;
        }

        // 4. 不能绑定自己
        if (bindMessage.getSelfNumber().equals(bindMessage.getTargetNumber())) {
            session.getBasicRemote().sendText(ResponseMessage.error("不能绑定自己").toJson());
            return;

        }

        // 4. 进行绑定
        self.setTarget(target.getSelf());
        target.setTarget(self.getSelf());

        // 5. 写回数据
        session.getBasicRemote()
                .sendText(new ServerBindMessage(bindMessage.getTargetNumber()).toJson());
        target.getSelf()
                .getSession()
                .getBasicRemote()
                .sendText(new ServerBindMessage(bindMessage.getSelfNumber()).toJson());
    }


    /**
     * 处理电影信息
     *
     * @param message 消息
     */
    private void doMovie(Message message) {

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

    public Session getSession() {
        return this.session;
    }
}
