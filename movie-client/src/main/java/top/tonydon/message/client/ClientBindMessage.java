package top.tonydon.message.client;


import top.tonydon.util.MessageType;

/**
 * 绑定消息，将 selfNumber 和 targetNumber 在服务端绑定起来
 */
public class ClientBindMessage extends ClientMessage {

    private String targetNumber;

    public ClientBindMessage() {
    }

    public ClientBindMessage(String selfNumber, String targetNumber) {
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
        return MessageType.CLIENT_BIND;
    }

    @Override
    public String toString() {
        return "BindMessage{" +
                "targetNumber='" + targetNumber + '\'' +
                "} " + super.toString();
    }
}
