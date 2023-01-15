package top.tonydon.websocket;

import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import top.tonydon.message.JsonMessage;
import top.tonydon.message.Message;
import top.tonydon.message.MessageType;
import top.tonydon.message.common.ActionMessage;
import top.tonydon.util.StrUtils;

import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

// todo 放映室，用于多人同步播放
@ServerEndpoint(value = "/projection-room")
@Component
public class ProjectionRoomItem {
    private static final Logger log = LoggerFactory.getLogger(ProjectionRoomItem.class);

    private static final Set<String> ROOM_ID_SET = new ConcurrentSkipListSet<>();

    private Session session;
    private String roomId;

    @OnOpen
    public void onOpen(Session session){
        this.session = session;


    }


    @OnMessage
    public void onMessage(String json){
        Message message = JsonMessage.parse(json);

        // ActionMessage
        if (message.getType() == MessageType.ACTION){
            ActionMessage actionMessage = (ActionMessage) message;


        }
    }



    private void createRoom() {
        // 创建 RoomID
        roomId = StrUtils.randomNum(8);
        while (ROOM_ID_SET.contains(roomId))
            roomId = StrUtils.randomNum(8);

    }

}
