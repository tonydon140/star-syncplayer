package top.tonydon.syncplayer.websocket;

import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
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
import top.tonydon.syncplayer.util.SocketRoom;
import top.tonydon.syncplayer.util.StrUtils;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

// todo 放映室，用于多人同步播放
@ServerEndpoint(value = "/projection-room")
@Component
public class ProjectionRoomItem {
    private static final Logger log = LoggerFactory.getLogger(ProjectionRoomItem.class);

    private static final int ROOM_ID_LENGTH = 6;
    private static final int ID_LENGTH = 6;

    // 房间 Map
    private static final Map<String, SocketRoom> ROOM_MAP = new ConcurrentHashMap<>();

    // 用户 ID Set
    private static final Set<String> ID_SET = new ConcurrentSkipListSet<>();

    private Session session;
    private String roomId;
    private String id;


    private void sendAsync(String content) {
        this.session.getAsyncRemote().sendText(content);
    }

    private void sendAsync(Message message) {
        this.session.getAsyncRemote().sendText(message.toJson());
    }


    @OnOpen
    public void onOpen(Session session) {
        this.session = session;

        // 创建 id
        this.id = StrUtils.randomNum(ID_LENGTH);
        while (ID_SET.contains(this.id))
            this.id = StrUtils.randomNum(ID_LENGTH);
        ID_SET.add(this.id);

        log.info("item add --- {}", this.id);
    }

    // 房间一旦创建，直至房间里所有人退出，房间才会自动关闭
    @OnClose
    public void onClose() {
        if (roomId != null) {
            SocketRoom room = ROOM_MAP.get(roomId);
            room.remove(this);
            // 房间已经没有人了，则删除房间
            if (room.size() == 0) {
                ROOM_MAP.remove(roomId);
            }
        }
        // 从 ID_SET 中删除
        ID_SET.remove(this.id);
        log.info("item closed --- {}", this.id);
    }


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

        log.info("{} --- {}", this.id, json);
    }

    private void doAction(int actionCode) {
        if (actionCode == ActionCode.CREATE_ROOM) {
            createRoom();
        } else if (actionCode == ActionCode.QUIT_ROOM) {
            quitRoom();
        }
    }


    private void doString(int actionCode, String content) {
        if (actionCode == ActionCode.ADD_ROOM) {
            addRoom(content);
        } else if (actionCode == ActionCode.CHAT) {
            doChat(content);
        }
    }


    private void doMovie(String json) {

    }


    private void createRoom() {
        // 创建 RoomID
        roomId = StrUtils.randomNum(ROOM_ID_LENGTH);
        while (ROOM_MAP.containsKey(roomId))
            roomId = StrUtils.randomNum(ROOM_ID_LENGTH);

        // 创建 Room
        SocketRoom room = new SocketRoom();
        room.add(this);
        ROOM_MAP.put(roomId, room);

        // 写回消息
        sendAsync(StringMessage.ROOM_CREATED.setContent(roomId));
        log.info("room created, room id = {}", roomId);
    }

    private void quitRoom() {
        if (roomId == null) {
            log.error("quit room --- 房间号不存在");
            return;
        }

        SocketRoom room = ROOM_MAP.get(roomId);
        if (room == null) {
            log.error("quit room --- 放映室不存在");
            return;
        }

        // 退出放映室
        room.remove(this);
        // 如果放映室为空，则删除放映室
        if (room.size() == 0) {
            ROOM_MAP.remove(roomId);
        }
        roomId = null;
        log.info("{} --- quit room", this.id);
    }

    private void addRoom(String roomId) {
        // 放映室 ID 不存在
        if (!ROOM_MAP.containsKey(roomId)) {
            sendAsync(StringMessage.SERVER_RESPONSE.setContent("放映室不存在"));
            return;
        }

        // 加入放映室
        SocketRoom room = ROOM_MAP.get(roomId);
        room.add(this);
        this.roomId = roomId;

        // todo 发送加入成功
//        ActionCode.C

        // 通知放映室中的其他人
        String content = this.id + " 加入了放映室！";
        room.items().forEach(item -> item.sendAsync(StringMessage.CHAT.setContent(content)));
    }

    // 处理聊天信息
    private void doChat(String content) {
        if (roomId == null) {
            sendAsync(StringMessage.SERVER_RESPONSE.setContent("未加入放映室"));
            log.error("chat --- 未加入放映室");
            return;
        }

        SocketRoom room = ROOM_MAP.get(roomId);
        if (room == null) {
            sendAsync(StringMessage.SERVER_RESPONSE.setContent("放映室不存在"));
            log.error("chat --- 放映室不存在");
            return;
        }

        String text = this.id + "：" + content;
        room.items().forEach(item -> item.sendAsync(StringMessage.CHAT.setContent(text)));
    }


}
