package top.tonydon.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import top.tonydon.message.client.ClientBindMessage;
import top.tonydon.message.client.ClientUnbindMessage;
import top.tonydon.message.server.*;
import top.tonydon.message.JsonMessage;
import top.tonydon.message.Message;
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

    private String number;

    // 处理连接建立
    @OnOpen
    public void onOpen(Session session) {
        // 1. 生成星星号
        ServerConnectMessage message = ServerConnectMessage.success();

        // 2. 保存 session 和星星号
        this.session = session;
        this.number = message.getNumber();

        // 3. 存储到 map 中
        map.put(message.getNumber(), new WebSocketGroup(this, null));

        // 4. 返回连接消息
        try {
            session.getBasicRemote().sendText(message.toJson());
        } catch (IOException e) {
            e.printStackTrace();
        }

//        addCount();
        log.info("新的连接加入：{}", number);
    }

    //接受消息
    @OnMessage
    public void onMessage(String json, Session session) throws IOException {
        Message message = JsonMessage.parse(json);

        // 1. 绑定消息
        if (message.getType() == MessageType.CLIENT_BIND) doBind(message, session);
            // 2. 电影消息
        else if (message.getType() == MessageType.CLIENT_MOVIE) doMovie(message);
            // 3. 解除绑定
        else if (message.getType() == MessageType.CLIENT_UNBIND) doUnbind(message, session);

        log.info("{} --- {}", session.getId(), message);
    }

    private void doUnbind(Message message, Session session) throws IOException {
        ClientUnbindMessage clientUnbindMessage = (ClientUnbindMessage) message;

        // 获取组
        WebSocketGroup group = map.get(clientUnbindMessage.getSelfNumber());

        // 写回解除绑定数据
        ServerUnbindMessage unbindMessage = new ServerUnbindMessage();
        sendMessage(unbindMessage);
        sendTargetMessage(group.getTarget(), unbindMessage);

        // 删除组中的另一半
        group.setTarget(null);
    }

    /**
     * 处理绑定消息
     *
     * @param message 消息
     */
    private void doBind(Message message, Session session) throws IOException {
        ClientBindMessage clientBindMessage = (ClientBindMessage) message;

        // 1. 根据星星号获取组
        WebSocketGroup self = map.get(clientBindMessage.getSelfNumber());
        if (self == null) {
            session.getBasicRemote().sendText(ServerResponseMessage.error("本机星星号不存在").toJson());
            return;
        }

        // 3. 获取她/他的星星号
        WebSocketGroup target = map.get(clientBindMessage.getTargetNumber());
        if (target == null) {
            session.getBasicRemote().sendText(ServerResponseMessage.error("远程端星星号不存在").toJson());
            return;
        }

        // 4. 不能绑定自己
        if (clientBindMessage.getSelfNumber().equals(clientBindMessage.getTargetNumber())) {
            session.getBasicRemote().sendText(ServerResponseMessage.error("不能绑定自己").toJson());
            return;
        }

        // 4. 进行绑定
        self.setTarget(target.getSelf());
        target.setTarget(self.getSelf());

        // 5. 写回数据
        session.getBasicRemote()
                .sendText(new ServerBindMessage(clientBindMessage.getTargetNumber()).toJson());
        target.getSelf()
                .getSession()
                .getBasicRemote()
                .sendText(new ServerBindMessage(clientBindMessage.getSelfNumber()).toJson());
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
        // 如果客户端已建立连接，发送断开连接消息
        WebSocketGroup group = map.get(this.number);
        if (group.getTarget() != null)
            sendTargetMessage(group.getTarget(), new ServerOfflineMessage());

        map.remove(this.number);
        log.info("连接关闭:{}", this.number);
    }


    //发送消息
    public void sendMessage(Message message) {
        try {
            this.session.getBasicRemote().sendText(message.toJson());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendTargetMessage(WebSocket webSocket, Message message) {
        try {
            webSocket.getSession().getBasicRemote().sendText(message.toJson());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


//    //获取在线连接数目
//    public static int getCount() {
//        return count;
//    }
//
//    //操作count，使用synchronized确保线程安全
//    public static synchronized void addCount() {
//        WebSocket.count++;
//    }
//
//    public static synchronized void reduceCount() {
//        WebSocket.count--;
//    }

    public Session getSession() {
        return this.session;
    }
}
