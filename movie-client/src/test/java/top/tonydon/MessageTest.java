package top.tonydon;

import org.junit.jupiter.api.Test;
import top.tonydon.message.client.ClientMovieMessage;
import top.tonydon.message.server.ServerConnectMessage;
import top.tonydon.message.Message;
import top.tonydon.message.server.ServerOfflineMessage;
import top.tonydon.util.ActionCode;

public class MessageTest {

    @Test
    public void test(){
        Message message = new ClientMovieMessage("11111", ActionCode.SYNC, 6220, 1.2);
        System.out.println(message.toJson());
    }
}
