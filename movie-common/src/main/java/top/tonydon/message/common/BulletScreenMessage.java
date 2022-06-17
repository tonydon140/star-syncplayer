package top.tonydon.message.common;

import top.tonydon.message.Message;
import top.tonydon.message.MessageType;

/**
 * 弹幕消息
 */
public class BulletScreenMessage extends Message {

    private String content;

    public BulletScreenMessage() {
    }

    public BulletScreenMessage(String content) {
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
        return MessageType.BULLET_SCREEN;
    }

    @Override
    public String toString() {
        return "BulletScreenMessage{" +
                "content='" + content + '\'' +
                "} " + super.toString();
    }
}
