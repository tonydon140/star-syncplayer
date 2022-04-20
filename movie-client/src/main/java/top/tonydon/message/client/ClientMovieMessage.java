package top.tonydon.message.client;

import top.tonydon.util.MessageType;

public class ClientMovieMessage extends ClientMessage {

    private int actionCode;

    public ClientMovieMessage() {

    }

    public ClientMovieMessage(String selfNumber, int actionCode) {
        super(selfNumber);
        this.actionCode = actionCode;
    }

    @Override
    public int getType() {
        return MessageType.CLIENT_MOVIE;
    }

    public int getActionCode() {
        return actionCode;
    }

    public void setActionCode(int actionCode) {
        this.actionCode = actionCode;
    }

    @Override
    public String toString() {
        return "ClientMovieMessage{" +
                "actionCode=" + actionCode +
                "} " + super.toString();
    }
}
