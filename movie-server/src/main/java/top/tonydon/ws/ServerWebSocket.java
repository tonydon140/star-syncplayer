package top.tonydon.ws;

import jakarta.annotation.Resource;
import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import top.tonydon.constant.RedisConstants;
import top.tonydon.message.ActionCode;
import top.tonydon.message.JsonMessage;
import top.tonydon.message.Message;
import top.tonydon.message.MessageType;
import top.tonydon.message.common.BindMessage;
import top.tonydon.message.common.Notification;
import top.tonydon.message.server.ServerConnectMessage;
import top.tonydon.message.server.ServerResponseMessage;
import top.tonydon.util.RandomUtils;
import top.tonydon.util.SocketGroup;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// 接受 websocket 请求路径
@ServerEndpoint(value = "/websocket")
// 注册到 spring 容器中
@Component
public class ServerWebSocket {
    private static final Logger log = LoggerFactory.getLogger(ServerWebSocket.class);

    private static StringRedisTemplate stringRedisTemplate;

    // 通过 set() 方法注入静态的 StringRedisTemplate
    @Resource
    public void setStringRedisTemplate(StringRedisTemplate template) {
        ServerWebSocket.stringRedisTemplate = template;
    }

    /**
     * key：星星号
     * value：WebSocketGroup
     */
    private static final Map<String, SocketGroup> groupMap = new ConcurrentHashMap<>();


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
        while (groupMap.containsKey(number))
            number = RandomUtils.randomNumbers(8);

        // 保存 session
        this.session = session;
        // 存储到 map 中
        groupMap.put(number, new SocketGroup(this, null));
        // 返回连接消息
        sendMessage(new ServerConnectMessage(number));

        log.info("新的连接加入: {}", number);
        // 更新 Redis
        increment();
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
        SocketGroup group = groupMap.get(number);
        if (group.getFriendSocket() != null) {
            // 通知对方已经下线
            group.sendFriend(new Notification(ActionCode.OFFLINE));
            // 删除对方 group 中的自己
            String friendNumber = group.getFriendSocket().number;
            groupMap.get(friendNumber).setFriendSocket(null);
        }
        // 从 map 中删除自己
        groupMap.remove(number);
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
        SocketGroup ownGroup = groupMap.get(number);

        // 解除绑定
        if (code == ActionCode.UNBIND) {
            // 向对方发送解除绑定数据
            ownGroup.sendFriend(message);
            // 删除对方组中的自己
            String friendNumber = ownGroup.getFriendSocket().number;
            groupMap.get(friendNumber).removeFriendSocket();
            // 删除自己组中的对方
            ownGroup.removeFriendSocket();
        }
    }


    /**
     * 处理绑定消息
     *
     * @param message 消息
     */
    private void doBind(Message message) {
        BindMessage bindMessage = (BindMessage) message;
        String friendNumber = bindMessage.getTargetNumber();

        // 1. 根据星星号获取组
        SocketGroup ownGroup = groupMap.get(number);
        if (ownGroup == null) {
            sendMessage(ServerResponseMessage.error("未找到您的星星号！"));
            return;
        }

        // 3. 获取她/他的星星号
        SocketGroup friendGroup = groupMap.get(friendNumber);
        if (friendGroup == null) {
            sendMessage(ServerResponseMessage.error("星星号不存在！"));
            return;
        }

        // 4. 不能绑定自己
        if (number.equals(friendNumber)) {
            sendMessage(ServerResponseMessage.error("不能绑定自己"));
            return;
        }

        // 4. 进行绑定
        ownGroup.setFriendSocket(friendGroup.getOwnSocket());
        friendGroup.setFriendSocket(ownGroup.getOwnSocket());

        // 写回数据，自己绑定对方
        sendMessage(new BindMessage(friendNumber));
        // 对方绑定自己
        ownGroup.sendFriend(new BindMessage(number));
    }


    /**
     * 处理电影信息
     *
     * @param json 消息
     */
    private void doMovie(String json) {
        // 1. 获取消息组
        SocketGroup group = groupMap.get(number);

        // 3. 向双方写回消息
        sendMessage(json);
        group.sendFriend(json);
    }

    /**
     * 处理弹幕消息，服务端接收一方发送的弹幕，发送给另一方
     *
     * @param message 弹幕消息
     */
    private void doBulletScreen(Message message) {
        // 直接将弹幕消息发生给另一方
        SocketGroup group = groupMap.get(number);
        group.sendFriend(message);
    }


    //发送消息
    private void sendMessage(Message message) {
        sendMessage(message.toJson());
    }

    private void sendMessage(String message) {
        try {
            this.session.getBasicRemote().sendText(message);
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
        return groupMap.size();
    }

    public static Map<String, String> getMap() {
        Map<String, String> group = new HashMap<>();
        for (String key : groupMap.keySet()) {
            ServerWebSocket friendSocket = groupMap.get(key).getFriendSocket();
            if (friendSocket == null) {
                group.put(key, null);
            } else {
                group.put(key, friendSocket.number);
            }
        }
        return group;
    }


    public Session getSession() {
        return session;
    }


    private void increment() {
        // 当前时间
        LocalDate nowDate = LocalDate.now();
        // 最大保存日期
        LocalDate pastDate = nowDate.minusDays(RedisConstants.HISTORY_DAYS);

        // 自增
        stringRedisTemplate.opsForHash().increment(
                RedisConstants.KEY_CONNECTION_COUNT,
                nowDate.toString(),
                RedisConstants.INCREMENT_DELTA);

        // 删除最大保存日期
        stringRedisTemplate.opsForHash().delete(RedisConstants.KEY_CONNECTION_COUNT, pastDate.toString());
    }
}
