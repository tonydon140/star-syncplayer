package top.tonydon.message.server;

import top.tonydon.message.Message;
import top.tonydon.util.MessageType;

public class ServerMovieMessage extends Message {

    private int actionCode;

    public ServerMovieMessage() {
    }

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

    @Override
    public String toString() {
        return "ServerMovieMessage{" +
                "actionCode=" + actionCode +
                "} " + super.toString();
    }
}
