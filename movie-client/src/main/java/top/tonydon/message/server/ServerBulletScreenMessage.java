package top.tonydon.message.server;

import top.tonydon.message.Message;
import top.tonydon.util.MessageType;

public class ServerBulletScreenMessage extends Message {

    private String content;

    public ServerBulletScreenMessage() {
    }

    public ServerBulletScreenMessage(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public int getType() {
        return MessageType.SERVER_BULLET_SCREEN;
    }

    @Override
    public String toString() {
        return "ServerBulletScreenMessage{" +
                "content='" + content + '\'' +
                "} " + super.toString();
    }
}
