package top.tonydon.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import top.tonydon.message.Message;
import top.tonydon.ws.ServerWebSocket;

import java.io.IOException;

/**
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WebSocketGroup {
    private ServerWebSocket self;
    private ServerWebSocket target;

    public void sendTarget(String content) {
        try {
            target.getSession().getBasicRemote().sendText(content);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendTarget(Message message) {
        try {
            target.getSession().getBasicRemote().sendText(message.toJson());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
