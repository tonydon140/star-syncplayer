package top.tonydon.syncplayer.client;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.tonydon.syncplayer.message.JsonMessage;
import top.tonydon.syncplayer.message.Message;
import top.tonydon.syncplayer.message.MessageType;
import top.tonydon.syncplayer.message.common.ActionMessage;
import top.tonydon.syncplayer.message.common.MovieMessage;
import top.tonydon.syncplayer.message.common.StringMessage;
import top.tonydon.syncplayer.util.observer.ClientObserver;
import top.tonydon.syncplayer.util.observer.Observable;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;

public class WebClient extends WebSocketClient implements Observable<ClientObserver> {
    private static final Logger log = LoggerFactory.getLogger(WebClient.class);

    // 观察者集合
    private final Set<ClientObserver> observerSet = new HashSet<>();

    public WebClient(URI serverUri) {
        super(serverUri);
    }

    // 连接建立
    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        log.info("连接服务器成功！");
    }


    // 收到消息
    @Override
    public void onMessage(String json) {
        // 1. 转换 json 到对象
        Message message = JsonMessage.parse(json);
//        log.debug(json);

        // 2. 对不同的消息执行不同的处理
        // ActionMessage
        if (message.getType() == MessageType.ACTION) {
            int code = ((ActionMessage) message).getActionCode();
            observerSet.forEach(clientObserver -> clientObserver.onAction(code));
        }

        // StringMessage
        else if (message.getType() == MessageType.STRING) {
            StringMessage stringMessage = (StringMessage) message;
            int code = stringMessage.getActionCode();
            String content = stringMessage.getContent();
            observerSet.forEach(clientObserver -> clientObserver.onString(code, content));
        }

        // MovieMessage
        else if (message.getType() == MessageType.MOVIE) {
            MovieMessage movieMessage = (MovieMessage) message;
            observerSet.forEach(clientObserver -> clientObserver.onMovie(movieMessage));
        }
    }


    // 连接关闭
    @Override
    public void onClose(int code, String reason, boolean remote) {
        log.warn("code = {}, reason = {}", code, reason);
    }


    // 发生异常
    @Override
    public void onError(Exception ex) {
        ex.printStackTrace();
        log.error(ex.getMessage());
    }


    // 添加观察者
    @Override
    public void addObserver(ClientObserver observer) {
        observerSet.add(observer);
    }


    // 删除观察者
    @Override
    public void removeObserver(ClientObserver observer) {
        observerSet.remove(observer);
    }
}
