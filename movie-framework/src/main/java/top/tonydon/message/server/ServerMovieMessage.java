package top.tonydon.message.server;

import lombok.Data;
import lombok.EqualsAndHashCode;
import top.tonydon.message.Message;
import top.tonydon.util.MessageType;

@EqualsAndHashCode(callSuper = true)
@Data
public class ServerMovieMessage extends Message {

    private int actionCode;

    public ServerMovieMessage(int actionCode) {
        this.actionCode = actionCode;
    }


    @Override
    public int getType() {
        return MessageType.SERVER_MOVIE;
    }

    public int getActionCode() {
        return actionCode;
    }

    public void setActionCode(int actionCode) {
        this.actionCode = actionCode;
    }
}
