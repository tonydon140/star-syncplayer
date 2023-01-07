package top.tonydon.ws;

import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import top.tonydon.message.ActionCode;
import top.tonydon.message.JsonMessage;
import top.tonydon.message.Message;
import top.tonydon.message.MessageType;
import top.tonydon.message.common.BindMessage;
import top.tonydon.message.common.Notification;
import top.tonydon.message.server.ServerConnectMessage;
import top.tonydon.message.server.ServerResponseMessage;
import top.tonydon.util.RandomUtils;
import top.tonydon.util.WebSocketGroup;

import java.io.IOException;
import java.util.HashMap;
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
        // 生成不重复的星星号，存储在 this.number 中
        number = RandomUtils.randomNumbers(8);
        while (map.containsKey(number)) number = RandomUtils.randomNumbers(8);

        // 创建连接消息
        ServerConnectMessage message = new ServerConnectMessage(number);

        // 保存 session
        this.session = session;

        // 存储到 map 中
        map.put(number, new WebSocketGroup(this, null));

        // 返回连接消息
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

        // 绑定消息
        if (message.getType() == MessageType.BIND) doBind(message);
            // 电影消息
        else if (message.getType() == MessageType.MOVIE) doMovie(json);
            // 解除绑定
        else if (message.getType() == MessageType.NOTIFICATION) doNotification(message);
            // 弹幕消息
        else if (message.getType() == MessageType.BULLET_SCREEN) doBulletScreen(message);
        log.info("{} --- {}", number, json);
    }


    // 处理错误
    @OnError
    public void onError(Throwable error, Session session) {
        log.info("Error --- {}", number);
        error.printStackTrace();
    }

    // 处理连接关闭
    @OnClose
    public void onClose() {
        // 如果客户端已建立连接，发送断开连接消息
        WebSocketGroup group = map.get(number);
//        log.debug("target = {}",group.getTarget());
        if (group.getTarget() != null) {
            // 通知对方已经下线
            group.sendTarget(new Notification(ActionCode.OFFLINE));
            // 删除对方 group 中的自己
            String targetNumber = group.getTarget().number;
            map.get(targetNumber).setTarget(null);

//            log.debug("对方中的自己：{}", map.get(targetNumber).getTarget());
        }
        // 从 map 中删除自己
        map.remove(number);
        log.info("连接关闭: {}", number);
    }

    /**
     * 解除绑定
     *
     * @param message 消息
     */
    private void doNotification(Message message) {
        // 获得通知类型
        int code = ((Notification) message).getActionCode();
        // 获取组
        WebSocketGroup group = map.get(number);

        // 解除绑定
        if (code == ActionCode.UNBIND) {
            // 向对方发送解除绑定数据
            group.sendTarget(message);
            // 删除对方组中的自己
            map.get(group.getTarget().number).setTarget(null);
            // 删除自己组中的对方
            group.setTarget(null);
        }
    }

    /**
     * 处理绑定消息
     *
     * @param message 消息
     */
    private void doBind(Message message) {
        BindMessage bindMessage = (BindMessage) message;
        String targetNumber = bindMessage.getTargetNumber();

        // 1. 根据星星号获取组
        WebSocketGroup self = map.get(number);
        if (self == null) {
            sendMessage(ServerResponseMessage.error("本机星星号不存在"));
            return;
        }

        // 3. 获取她/他的星星号
        WebSocketGroup target = map.get(targetNumber);
        if (target == null) {
            sendMessage(ServerResponseMessage.error("远程端星星号不存在"));
            return;
        }

        // 4. 不能绑定自己
        if (number.equals(targetNumber)) {
            sendMessage(ServerResponseMessage.error("不能绑定自己"));
            return;
        }

        // 4. 进行绑定
        self.setTarget(target.getSelf());
        target.setTarget(self.getSelf());

        // 写回数据，自己绑定对方
        sendMessage(new BindMessage(targetNumber));
        // 对方绑定自己
        self.sendTarget(new BindMessage(number));
    }


    /**
     * 处理电影信息
     *
     * @param json 消息
     */
    private void doMovie(String json) {
        // 1. 获取消息组
        WebSocketGroup group = map.get(number);

        // 3. 向双方写回消息
        sendMessage(json);
        group.sendTarget(json);
    }

    /**
     * 处理弹幕消息，服务端接收一方发送的弹幕，发送给另一方
     *
     * @param message 弹幕消息
     */
    private void doBulletScreen(Message message) {
        // 直接将弹幕消息发生给另一方
        WebSocketGroup group = map.get(number);
        group.sendTarget(message);
    }


    //发送消息
    private void sendMessage(Message message) {
        try {
            session.getBasicRemote().sendText(message.toJson());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage(String message) {
        try {
            session.getBasicRemote().sendText(message);
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

    public static Map<String, String> getMap() {
        Map<String, String> group = new HashMap<>();
        for (String key : map.keySet()) {
            ServerWebSocket target = map.get(key).getTarget();
            if (target == null) {
                group.put(key, null);
            } else {
                group.put(key, target.number);
            }
        }
        return group;
    }

    public Session getSession() {
        return session;
    }
}
