package top.tonydon.message.server;


import top.tonydon.message.Message;
import top.tonydon.util.MessageType;

/**
 * 服务端绑定客户端消息
 */
public class ServerBindMessage extends Message {
    private String targetNumber;

    public ServerBindMessage() {
    }

    public ServerBindMessage(String targetNumber) {
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
        return MessageType.SERVER_BIND;
    }

    @Override
    public String toString() {
        return "ServerBindMessage{" +
                "targetNumber='" + targetNumber + '\'' +
                "} " + super.toString();
    }
}
