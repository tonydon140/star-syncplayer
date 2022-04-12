package top.tonydon.client;

import lombok.extern.slf4j.Slf4j;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import top.tonydon.message.server.ConnectMessage;
import top.tonydon.message.JsonMessage;
import top.tonydon.message.Message;
import top.tonydon.message.server.ServerBindMessage;
import top.tonydon.util.ClientObserver;
import top.tonydon.util.MessageType;
import top.tonydon.util.Observable;


import java.net.URI;
import java.util.HashSet;
import java.util.Set;

@Slf4j
public class WebClient extends WebSocketClient implements Observable {
    private final Set<ClientObserver> observerSet = new HashSet<>();

    private String number;

    public WebClient(URI serverUri) {
        super(serverUri);
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
//        log.debug("连接服务器成功！");
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
        log.info("message = {}", message);

        // 2. 对不同的消息执行不同的处理
        if (message.getType() == MessageType.CONNECT_TYPE) {
            // 设置星星码
            ConnectMessage connectMessage = (ConnectMessage) message;
            this.number = connectMessage.getNumber();
            // 遍历观察者
            observerSet.forEach(observer -> observer.onConnected(connectMessage));
        }

        // 绑定处理
        else if(message.getType() == MessageType.SERVER_BIND_TYPE){
            ServerBindMessage serverBindMessage = (ServerBindMessage) message;
            observerSet.forEach(clientObserver -> clientObserver.onBind(serverBindMessage));
        }

    }

    @Override
    public void onClose(int code, String reason, boolean remote) {

    }

    @Override
    public void onError(Exception ex) {

    }


    public String getNumber(){
        return number;
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
