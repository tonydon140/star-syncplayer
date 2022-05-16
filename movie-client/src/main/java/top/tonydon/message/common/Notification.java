package top.tonydon.message.common;

import top.tonydon.message.Message;
import top.tonydon.util.MessageType;

/**
 * 通知，表示没有实质内容，只表达含义的消息。如解除绑定、下线等
 * 其含义通过 actionCode 表达
 */
public class Notification extends Message {

    private int actionCode;

    public Notification() {
    }

    public Notification(int actionCode) {
        this.actionCode = actionCode;
    }

    @Override
    public int getType() {
        return MessageType.NOTIFICATION;
    }

    public int getActionCode() {
        return actionCode;
    }

    public void setActionCode(int actionCode) {
        this.actionCode = actionCode;
    }

    @Override
    public String toString() {
        return "Notification{" +
                "actionCode=" + actionCode +
                "} " + super.toString();
    }
}
