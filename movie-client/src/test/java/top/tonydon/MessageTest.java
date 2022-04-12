package top.tonydon;

import org.junit.jupiter.api.Test;
import top.tonydon.message.server.ConnectMessage;
import top.tonydon.message.Message;
import top.tonydon.message.client.BindMessage;

public class MessageTest {

    @Test
    public void test(){
        Message message = new ConnectMessage();

        System.out.println(message.getType());
//        System.out.println(MessageEnum.g);

    }
}
