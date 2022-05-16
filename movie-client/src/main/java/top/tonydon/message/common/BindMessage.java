package top.tonydon.message.client;

import top.tonydon.message.Message;
import top.tonydon.util.MessageType;

/**
 * 绑定消息
 */
public class BindMessage extends Message {
    private String targetNumber;

    public BindMessage() {
    }

    public BindMessage(String targetNumber) {
        this.targetNumber = targetNumber;
    }

    public String getTargetNumber() {
        return targetNumber;
    }

    public void setTargetNumber(String targetNumber) {
        this.targetNumber = targetNumber;
    }

    @Override
    public String toString() {
        return "BindMessage{" +
                "targetNumber='" + targetNumber + '\'' +
                "} " + super.toString();
    }

    @Override
    public int getType() {
        return MessageType.BIND;
    }
}
