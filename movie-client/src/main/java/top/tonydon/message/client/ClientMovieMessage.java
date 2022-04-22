package top.tonydon.message.client;

import javafx.util.Duration;
import top.tonydon.util.MessageType;

/**
 * 客户端发送控制电影相关的信息
 * actionCode：动作码
 * duration：当前视频的时间
 */
public class ClientMovieMessage extends ClientMessage {

    private int actionCode;

    private double seconds;

    public ClientMovieMessage() {

    }

    public ClientMovieMessage(String selfNumber, int actionCode) {
        super(selfNumber);
        this.actionCode = actionCode;
    }

    public ClientMovieMessage(String selfNumber, int actionCode, double seconds) {
        super(selfNumber);
        this.actionCode = actionCode;
        this.seconds = seconds;
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

    public double getSeconds() {
        return seconds;
    }

    public void setSeconds(double seconds) {
        this.seconds = seconds;
    }

    @Override
    public String toString() {
        return "ClientMovieMessage{" +
                "actionCode=" + actionCode +
                ", seconds=" + seconds +
                "} " + super.toString();
    }
}
