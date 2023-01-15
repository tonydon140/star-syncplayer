package top.tonydon.message.common;

import top.tonydon.message.ActionCode;
import top.tonydon.message.Message;
import top.tonydon.message.MessageType;

/**
 * 字符串消息，携带一个字符串和动作类型的消息。
 */
public class StringMessage extends Message {

    public static final StringMessage BIND = new StringMessage(ActionCode.BIND, null);
    public static final StringMessage CONNECTED = new StringMessage(ActionCode.CONNECTED, null);
    public static final StringMessage BULLET_SCREEN = new StringMessage(ActionCode.BULLET_SCREEN, null);
    public static final StringMessage SERVER_RESPONSE = new StringMessage(ActionCode.SERVER_RESPONSE, null);

    private int actionCode;
    private String content;

    public StringMessage() {

    }

    public StringMessage(int actionCode, String content) {
        this.actionCode = actionCode;
        this.content = content;
    }

    public int getActionCode() {
        return actionCode;
    }

    public StringMessage setActionCode(int actionCode) {
        this.actionCode = actionCode;
        return this;
    }

    public String getContent() {
        return content;
    }

    public StringMessage setContent(String content) {
        this.content = content;
        return this;
    }

    @Override
    public int getType() {
        return MessageType.STRING;
    }

    @Override
    public String toString() {
        return "StringMessage{" +
                "actionCode=" + actionCode +
                ", content='" + content + '\'' +
                "} " + super.toString();
    }
}
