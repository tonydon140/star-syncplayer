package top.tonydon.message.server;

import lombok.Data;
import lombok.EqualsAndHashCode;
import top.tonydon.message.Message;
import top.tonydon.util.MessageType;

@EqualsAndHashCode(callSuper = true)
@Data
public class ResponseMessage extends Message {

    /**
     * 操作是否成功
     */
    private boolean success;

    /**
     * 响应信息
     */
    private String msg;

    public ResponseMessage(boolean success, String msg) {
        this.success = success;
        this.msg = msg;
    }

    public static ResponseMessage error(String msg) {
        return new ResponseMessage(false, msg);
    }

    public static ResponseMessage success(String msg) {
        return new ResponseMessage(true, msg);
    }

    @Override
    public int getType() {
        return MessageType.RESPONSE_TYPE;
    }
}
