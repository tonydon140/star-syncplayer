package top.tonydon.message.server;

import top.tonydon.message.Message;
import top.tonydon.message.MessageType;

public class ServerResponseMessage extends Message {

    /**
     * 操作是否成功
     */
    private boolean success;

    /**
     * 响应信息
     */
    private String msg;

    public ServerResponseMessage() {
    }

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

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "ServerResponseMessage{" +
                "success=" + success +
                ", msg='" + msg + '\'' +
                "} " + super.toString();
    }
}
