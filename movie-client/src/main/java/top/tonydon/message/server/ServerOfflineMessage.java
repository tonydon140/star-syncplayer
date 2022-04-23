package top.tonydon.message.server;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import top.tonydon.message.Message;
import top.tonydon.util.MessageType;

@JsonSerialize
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
