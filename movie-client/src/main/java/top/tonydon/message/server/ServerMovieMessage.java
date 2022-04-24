package top.tonydon.message.server;

import javafx.util.Duration;
import top.tonydon.message.Message;
import top.tonydon.util.MessageType;

public class ServerMovieMessage extends Message {

    private int actionCode;
    private double seconds;
    private double rate;

    public ServerMovieMessage() {
    }

    public ServerMovieMessage(int actionCode) {
        this.actionCode = actionCode;
    }

    public ServerMovieMessage(int actionCode, double seconds, double rate) {
        this.actionCode = actionCode;
        this.seconds = seconds;
        this.rate = rate;
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

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    @Override
    public String toString() {
        return "ServerMovieMessage{" +
                "actionCode=" + actionCode +
                ", seconds=" + seconds +
                ", rate=" + rate +
                "} " + super.toString();
    }
}
