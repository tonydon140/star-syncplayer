package top.tonydon.message.server;

import lombok.Data;
import lombok.EqualsAndHashCode;
import top.tonydon.message.Message;
import top.tonydon.util.MessageType;

@EqualsAndHashCode(callSuper = true)
@Data
public class ServerResponseMessage extends Message {

    /**
     * 操作是否成功
     */
    private boolean success;

    /**
     * 响应信息
     */
    private String msg;

    public ServerResponseMessage(boolean success, String msg) {
        this.success = success;
        this.msg = msg;
    }

    public static ServerResponseMessage error(String msg) {
        return new ServerResponseMessage(false, msg);
    }

    public static ServerResponseMessage success(String msg) {
        return new ServerResponseMessage(true, msg);
    }

    @Override
    public int getType() {
        return MessageType.SERVER_RESPONSE;
    }
}
