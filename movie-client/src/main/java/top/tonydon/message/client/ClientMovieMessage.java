package top.tonydon.message.client;

import top.tonydon.util.MessageType;

/**
 * 客户端发送控制电影相关的信息
 * actionCode：动作码
 * duration：当前视频的时间
 * rate: 当前视频的部分速度
 */
public class ClientMovieMessage extends ClientMessage {

    private int actionCode;

    private double seconds;

    private double rate;

    public ClientMovieMessage() {

    }

    public ClientMovieMessage(String selfNumber, int actionCode) {
        super(selfNumber);
        this.actionCode = actionCode;
    }

    public ClientMovieMessage(String selfNumber, int actionCode, double seconds, double rate) {
        super(selfNumber);
        this.actionCode = actionCode;
        this.seconds = seconds;
        this.rate = rate;
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

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }


    @Override
    public String toString() {
        return "ClientMovieMessage{" +
                "actionCode=" + actionCode +
                ", seconds=" + seconds +
                ", rate=" + rate +
                "} " + super.toString();
    }
}
