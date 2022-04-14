package top.tonydon.message.server;

import lombok.Data;
import lombok.EqualsAndHashCode;
import top.tonydon.message.Message;
import top.tonydon.util.MessageType;

@EqualsAndHashCode(callSuper = true)
@Data
public class ServerOfflineMessage extends Message {
    @Override
    public int getType() {
        return MessageType.SERVER_OFFLINE;
    }
}
