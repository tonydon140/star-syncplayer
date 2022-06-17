package top.tonydon.client;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.tonydon.message.JsonMessage;
import top.tonydon.message.Message;
import top.tonydon.message.ActionCode;
import top.tonydon.message.common.BindMessage;
import top.tonydon.message.common.BulletScreenMessage;
import top.tonydon.message.common.MovieMessage;
import top.tonydon.message.common.Notification;
import top.tonydon.message.server.ServerConnectMessage;
import top.tonydon.util.observer.ClientObserver;
import top.tonydon.message.MessageType;
import top.tonydon.util.observer.Observable;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;

public class WebClient extends WebSocketClient implements Observable<ClientObserver> {
    private final Logger log = LoggerFactory.getLogger(WebClient.class);
    private final Set<ClientObserver> observerSet = new HashSet<>();

    /**
     * 自己的星星号
     */
    private String selfNumber;

    /**
     * 对方的星星号
     */
    private String targetNumber;

    /**
     * 是否已经和别人绑定
     */
    private boolean isBind;

    public WebClient(URI serverUri) {
        super(serverUri);
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        log.info("连接服务器成功！");
    }

    /**
     * 收到消息时执行方法
     *
     * @param json json 消息
     */
    @Override
    public void onMessage(String json) {
        // 1. 转换 json 到对象
        Message message = JsonMessage.parse(json);
        log.info("JsonMessage --- {}", json);

        // 2. 对不同的消息执行不同的处理
        if (message.getType() == MessageType.SERVER_CONNECT) {
            // 设置星星码
            ServerConnectMessage serverConnectMessage = (ServerConnectMessage) message;
            this.selfNumber = serverConnectMessage.getNumber();
            // 遍历观察者
            observerSet.forEach(observer -> observer.onConnected(serverConnectMessage));
        }

        // 绑定处理
        else if (message.getType() == MessageType.BIND) {
            BindMessage bindMessage = (BindMessage) message;
            this.targetNumber = bindMessage.getTargetNumber();
            observerSet.forEach(clientObserver -> clientObserver.onBind(bindMessage));
        }

        // 通知消息
        else if (message.getType() == MessageType.NOTIFICATION) {
            int code = ((Notification) message).getActionCode();
            // 另一半下线
            if (code == ActionCode.OFFLINE) observerSet.forEach(ClientObserver::onOffline);
            // 另一半解除绑定
            else if (code == ActionCode.UNBIND) observerSet.forEach(ClientObserver::onUnbind);
        }


        // 电影消息
        else if (message.getType() == MessageType.MOVIE) {
            MovieMessage movieMessage = (MovieMessage) message;
            observerSet.forEach(clientObserver -> clientObserver.onMovie(movieMessage));
        }

        // 弹幕消息
        else if (message.getType() == MessageType.BULLET_SCREEN) {
            BulletScreenMessage bulletScreenMessage = (BulletScreenMessage) message;
            observerSet.forEach(clientObserver -> clientObserver.onBulletScreen(bulletScreenMessage));
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {

    }

    @Override
    public void onError(Exception ex) {
        ex.printStackTrace();
    }


    public String getSelfNumber() {
        return selfNumber;
    }

    public String getTargetNumber() {
        return targetNumber;
    }

    public boolean isBind() {
        return isBind;
    }

    public void setBind(boolean isBind) {
        this.isBind = isBind;
    }

    /**
     * 添加观察者
     *
     * @param observer 观察者
     */
    @Override
    public void addObserver(ClientObserver observer) {
        observerSet.add(observer);
    }

    /**
     * 删除观察者
     *
     * @param observer 观察者对象
     */
    @Override
    public void removeObserver(ClientObserver observer) {
        observerSet.remove(observer);
    }
}
