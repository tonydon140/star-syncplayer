package top.tonydon.syncplayer.websocket;

import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import top.tonydon.syncplayer.message.ActionCode;
import top.tonydon.syncplayer.message.JsonMessage;
import top.tonydon.syncplayer.message.Message;
import top.tonydon.syncplayer.message.MessageType;
import top.tonydon.syncplayer.message.common.ActionMessage;
import top.tonydon.syncplayer.message.common.StringMessage;
import top.tonydon.syncplayer.util.StrUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// 接受 websocket 请求路径
@ServerEndpoint(value = "/syncplayer")
// 注册到 spring 容器中
@Component
public class SyncPlayerItem {
    private static final Logger log = LoggerFactory.getLogger(SyncPlayerItem.class);

    private static final int ID_LENGTH = 6;

    /**
     * key：id
     * value：SyncPlayerItem
     */
    public static final Map<String, SyncPlayerItem> PLAYER_ITEM_MAP = new ConcurrentHashMap<>();


    private Session session;
    private String id;
    public String friendId;

    private void sendAsync(String content) {
        this.session.getAsyncRemote().sendText(content);
    }

    private void sendAsync(Message message) {
        sendAsync(message.toJson());
    }


    // 处理连接建立
    @OnOpen
    public void onOpen(Session session) {
        this.session = session;

        // 生成星星号
        this.id = StrUtils.randomNum(ID_LENGTH);
        while (PLAYER_ITEM_MAP.containsKey(this.id))
            this.id = StrUtils.randomNum(ID_LENGTH);
        PLAYER_ITEM_MAP.put(id, this);

        // 返回连接消息
        sendAsync(StringMessage.CONNECTED.setContent(id));
        log.info("item connected, id = {}", id);
    }

    //接受消息
    @OnMessage
    public void onMessage(String json) {
        Message message = JsonMessage.parse(json);

        // 根据不同的消息类型进行处理
        switch (message.getType()) {
            case MessageType.ACTION -> doAction(((ActionMessage) message).getActionCode());
            case MessageType.STRING -> {
                StringMessage sm = (StringMessage) message;
                doString(sm.getActionCode(), sm.getContent());
            }
            case MessageType.MOVIE -> doMovie(json);
        }

        log.debug("id = {}, json = {}", id, json);
    }


    // 处理错误
    @OnError
    public void onError(Throwable error) {
        log.error("id = {}, error message = {}", id, error.getMessage());
        log.error("id = {}, error = {}", id, error);
        error.printStackTrace();
        // 发生异常关闭连接
        onClose();
    }

    // 处理连接关闭
    @OnClose
    public void onClose() {
        // 如果已建立连接，发送断开连接消息
        if (friendId != null) {
            // 通知对方已经下线
            SyncPlayerItem item = PLAYER_ITEM_MAP.get(friendId);
            if (item != null) {
                item.sendAsync(ActionMessage.OFFLINE);
                item.friendId = null;
            }
        }

        // 从 map 中删除自己
        PLAYER_ITEM_MAP.remove(id);
        log.info("item closed, id = {}", id);
    }

    // 处理 ActionMessage
    private void doAction(int actionCode) {
        if (actionCode == ActionCode.UNBIND) {
            doUnBind();
        } else if (actionCode == ActionCode.HEARTBEAT) {
            sendAsync(ActionMessage.HEARTBEAT);
        }
    }

    // 处理 StringMessage
    private void doString(int actionCode, String content) {
        if (actionCode == ActionCode.BIND) {
            doBind(content);
        } else if (actionCode == ActionCode.BULLET_SCREEN) {
            doBulletScreen(content);
        }
    }

    // 处理 MovieMessage
    private void doMovie(String json) {
        // 向双方写回消息
        PLAYER_ITEM_MAP.get(friendId).sendAsync(json);
        sendAsync(json);
    }


    // 解除绑定
    private void doUnBind() {
        // 向对方发送解除绑定数据
        SyncPlayerItem item = PLAYER_ITEM_MAP.get(this.friendId);
        item.sendAsync(ActionMessage.UNBIND);
        // 删除双方的 friendId
        item.friendId = null;
        this.friendId = null;
    }


    // 处理绑定消息
    private void doBind(String friendId) {
        // todo 校验星星号格式

        SyncPlayerItem item = PLAYER_ITEM_MAP.get(friendId);
        if (item == null) {
            sendAsync(StringMessage.SERVER_RESPONSE.setContent("星星号不存在！"));
            return;
        }

        // 4. 不能绑定自己
        if (this.id.equals(friendId)) {
            sendAsync(StringMessage.SERVER_RESPONSE.setContent("不能绑定自己"));
            return;
        }

        // 4. 进行绑定
        this.friendId = friendId;
        item.friendId = this.id;

        // 写回数据，自己绑定对方，对方绑定自己
        sendAsync(StringMessage.BIND.setContent(friendId));
        item.sendAsync(StringMessage.BIND.setContent(id));
    }


    /**
     * 处理弹幕消息，服务端接收一方发送的弹幕，发送给另一方
     */
    private void doBulletScreen(String content) {
        // 直接将弹幕消息发生给另一方
        PLAYER_ITEM_MAP.get(friendId).sendAsync(StringMessage.BULLET_SCREEN.setContent(content));
    }
}
