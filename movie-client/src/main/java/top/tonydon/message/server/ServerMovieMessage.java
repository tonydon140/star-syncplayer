package top.tonydon.message.server;

import javafx.util.Duration;
import top.tonydon.message.Message;
import top.tonydon.util.MessageType;

public class ServerMovieMessage extends Message {

    private int actionCode;
    private double seconds;

    public ServerMovieMessage() {
    }

    public ServerMovieMessage(int actionCode) {
        this.actionCode = actionCode;
    }

    public ServerMovieMessage(int actionCode, double seconds) {
        this.actionCode = actionCode;
        this.seconds = seconds;
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

    public double getSeconds() {
        return seconds;
    }

    public void setSeconds(double seconds) {
        this.seconds = seconds;
    }

    @Override
    public String toString() {
        return "ServerMovieMessage{" +
                "actionCode=" + actionCode +
                ", seconds=" + seconds +
                "} " + super.toString();
    }
}
