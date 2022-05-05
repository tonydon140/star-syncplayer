package top.tonydon.message.client;

import top.tonydon.util.MessageType;

public class ClientBulletScreenMessage extends ClientMessage {

    private String content;


    public ClientBulletScreenMessage() {
    }

    public ClientBulletScreenMessage(String selfNumber, String content) {
        super(selfNumber);
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
        return MessageType.CLIENT_BULLET_SCREEN;
    }

    @Override
    public String toString() {
        return "ClientBulletScreenMessage{" +
                "content='" + content + '\'' +
                "} " + super.toString();
    }
}
