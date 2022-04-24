package top.tonydon.ws;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import top.tonydon.message.client.*;
import top.tonydon.message.server.*;
import top.tonydon.message.JsonMessage;
import top.tonydon.message.Message;
import top.tonydon.util.MessageType;
import top.tonydon.util.WebSocketGroup;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// 接受 websocket 请求路径
@ServerEndpoint(value = "/websocket")
// 注册到 spring 容器中
@Component
@Slf4j
public class ServerWebSocket {

    /**
     * key：星星号
     * value：WebSocketGroup
     */
    private static final Map<String, WebSocketGroup> map = new ConcurrentHashMap<>();


    /**
     * 当前连接会话
     * 每个 WebSocket 连入都会创建一个 ServerWebSocket 实例
     */
    private Session session;

    /**
     * 当前连接客户端的星星号
     */
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

        log.info("新的连接加入: {}", number);
    }

    //接受消息
    @OnMessage
    public void onMessage(String json, Session session) {
        Message message = JsonMessage.parse(json);

        // 1. 绑定消息
        if (message.getType() == MessageType.CLIENT_BIND) doBind(message);
            // 2. 电影消息
        else if (message.getType() == MessageType.CLIENT_MOVIE) doMovie(message);
            // 3. 解除绑定
        else if (message.getType() == MessageType.CLIENT_UNBIND) doUnbind(message);

        log.info("{} --- {}", number, message);
    }


    //处理错误
    @OnError
    public void onError(Throwable error, Session session) {
        log.info("发生错误{}, {}", number, error.getMessage());
    }

    //处理连接关闭
    @OnClose
    public void onClose() {
        // 如果客户端已建立连接，发送断开连接消息
        WebSocketGroup group = map.get(this.number);
        if (group.getTarget() != null) {
            // 通知对方已经下线
            sendTargetMessage(group.getTarget(), new ServerOfflineMessage());
            // 删除对方 group 中的自己
            String targetNumber = group.getTarget().number;
            map.get(targetNumber).setTarget(null);
        }
        // 从 map 中删除自己
        map.remove(this.number);
        log.info("连接关闭: {}", this.number);
    }


    private void doUnbind(Message message) {
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
    private void doBind(Message message) {
        ClientBindMessage clientBindMessage = (ClientBindMessage) message;

        // 1. 根据星星号获取组
        WebSocketGroup self = map.get(clientBindMessage.getSelfNumber());
        if (self == null) {
            sendMessage(ServerResponseMessage.error("本机星星号不存在"));
            return;
        }

        // 3. 获取她/他的星星号
        WebSocketGroup target = map.get(clientBindMessage.getTargetNumber());
        if (target == null) {
            sendMessage(ServerResponseMessage.error("远程端星星号不存在"));
            return;
        }

        // 4. 不能绑定自己
        if (clientBindMessage.getSelfNumber().equals(clientBindMessage.getTargetNumber())) {
            sendMessage(ServerResponseMessage.error("不能绑定自己"));
            return;
        }

        // 4. 进行绑定
        self.setTarget(target.getSelf());
        target.setTarget(self.getSelf());

        // 5. 写回数据
        sendMessage(new ServerBindMessage(clientBindMessage.getTargetNumber()));
        sendTargetMessage(target.getSelf(), new ServerBindMessage(clientBindMessage.getSelfNumber()));
    }


    /**
     * 处理电影信息
     *
     * @param message 消息
     */
    private void doMovie(Message message) {
        // 1. 获取消息组
        ClientMovieMessage clientMessage = (ClientMovieMessage) message;
        WebSocketGroup group = map.get(clientMessage.getSelfNumber());

        // 2. 将 ClientMovieMessage 转为 ServerMovieMessage
        Message movieMessage = new ServerMovieMessage(
                clientMessage.getActionCode(),
                clientMessage.getSeconds(),
                clientMessage.getRate());

        // 3. 向双方写回消息
        sendMessage(movieMessage);
        sendTargetMessage(group.getTarget(), movieMessage);
    }


    //发送消息
    public void sendMessage(Message message) {
        try {
            session.getBasicRemote().sendText(message.toJson());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendTargetMessage(ServerWebSocket target, Message message) {
        try {
            target.session.getBasicRemote().sendText(message.toJson());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 获取当前在线的人数
     *
     * @return 在线人数
     */
    public static int getCount() {
        return map.size();
    }

    public static Map<String, WebSocketGroup> getMap() {
        return map;
    }
}
