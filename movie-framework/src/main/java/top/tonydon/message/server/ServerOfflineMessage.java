package top.tonydon.message.server;

import top.tonydon.message.Message;
import top.tonydon.util.MessageType;

public class ServerOfflineMessage extends Message {
    @Override
    public int getType() {
        return MessageType.SERVER_OFFLINE;
    }
}
