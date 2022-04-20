package top.tonydon.message.server;

import top.tonydon.message.Message;
import top.tonydon.util.MessageType;

import java.util.Random;

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

    public static ServerConnectMessage success() {
        // 随机生成 8 位数字字符串
        String number = randomNumbers();
        return new ServerConnectMessage(number);
    }

    @Override
    public int getType() {
        return MessageType.SERVER_CONNECT;
    }

    private static String randomNumbers() {
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 8; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return "ServerConnectMessage{" +
                "number='" + number + '\'' +
                "} " + super.toString();
    }
}
