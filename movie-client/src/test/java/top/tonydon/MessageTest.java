package top.tonydon;

import org.junit.jupiter.api.Test;
import top.tonydon.message.server.ServerConnectMessage;
import top.tonydon.message.Message;
import top.tonydon.message.server.ServerOfflineMessage;

public class MessageTest {

    @Test
    public void test(){
        ServerOfflineMessage message = new ServerOfflineMessage();
        System.out.println(message);
        System.out.println(message.toJson());
    }
}
