package top.tonydon;

import org.junit.jupiter.api.Test;
import top.tonydon.message.server.ServerConnectMessage;
import top.tonydon.message.Message;

public class MessageTest {

    @Test
    public void test(){
        Message message = new ServerConnectMessage();

        System.out.println(message.getType());
//        System.out.println(MessageEnum.g);

    }
}
