package top.tonydon.message.server;

import top.tonydon.message.Message;
import top.tonydon.message.MessageType;

/**
 * 连接消息，连接成功返回星星号
 */

public class ServerConnectMessage extends Message {

    private String number;

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public ServerConnectMessage() {
    }

    public ServerConnectMessage(String number) {
        this.number = number;
    }


    @Override
    public int getType() {
        return MessageType.SERVER_CONNECT;
    }

    @Override
    public String toString() {
        return "ServerConnectMessage{" +
                "number='" + number + '\'' +
                "} " + super.toString();
    }
}
