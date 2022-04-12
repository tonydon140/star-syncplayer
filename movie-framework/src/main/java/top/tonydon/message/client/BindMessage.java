package top.tonydon.message.client;


import lombok.Data;
import top.tonydon.util.MessageType;

/**
 * 绑定消息，将 selfNumber 和 targetNumber 在服务端绑定起来
 */
public class BindMessage extends ClientMessage {

    private String targetNumber;

    public BindMessage(String selfNumber, String targetNumber) {
        super(selfNumber);
        this.targetNumber = targetNumber;

    }

    public String getTargetNumber() {
        return targetNumber;
    }

    public void setTargetNumber(String targetNumber) {
        this.targetNumber = targetNumber;
    }

    @Override
    public int getType() {
        return MessageType.BIND_TYPE;
    }

    @Override
    public String toString() {
        return "BindMessage{" +
                "targetNumber='" + targetNumber + '\'' +
                "} " + super.toString();
    }
}
