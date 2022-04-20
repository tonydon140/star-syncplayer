package top.tonydon.message.server;


import top.tonydon.message.Message;
import top.tonydon.util.MessageType;


public class ServerOfflineMessage extends Message {
    public ServerOfflineMessage() {
    }

    @Override
    public int getType() {
        return MessageType.SERVER_OFFLINE;
    }

    @Override
    public String toString() {
        return "ServerOfflineMessage{} " + super.toString();
    }
}
